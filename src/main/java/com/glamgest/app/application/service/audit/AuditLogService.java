package com.glamgest.app.application.service.audit;

import com.glamgest.app.application.dto.audit.AuditLogPageDTO;
import com.glamgest.app.application.dto.audit.AuditLogResponseDTO;
import com.glamgest.app.infrastructure.persistence.entity.AuditLogs;
import com.glamgest.app.infrastructure.persistence.repository.JpaAuditLogRepository;
import com.glamgest.app.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AuditLogService {

    public static final String ERROR_MESSAGE_ATTRIBUTE = AuditLogService.class.getName() + ".errorMessage";
    private static final Pattern RESOURCE_ID = Pattern.compile("/([0-9]+)(?:/|$)");

    private final JpaAuditLogRepository repository;
    private final UserRepository userRepository;
    private final int retentionDays;

    public AuditLogService(JpaAuditLogRepository repository,
                           UserRepository userRepository,
                           @Value("${audit.retention-days:365}") int retentionDays) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.retentionDays = retentionDays;
    }

    @Transactional
    public void record(HttpServletRequest request, int statusCode, Throwable failure) {
        String path = request.getRequestURI();
        if (!path.startsWith("/api/") || path.startsWith("/api/auth/")
                || "GET".equalsIgnoreCase(request.getMethod())
                || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return;
        }

        AuditLogs log = new AuditLogs();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.setOccurredAt(LocalDateTime.now());
        log.setHttpMethod(request.getMethod());
        log.setEndpoint(path);
        log.setStatusCode(statusCode);
        log.setAction(actionFor(request.getMethod(), statusCode));
        log.setModule(moduleFor(path));
        log.setResourceId(resourceIdFor(path));
        log.setIpAddress(request.getRemoteAddr());
        log.setUserAgent(trim(request.getHeader("User-Agent"), 500));

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            log.setUserEmail(authentication.getName());
            log.setRole(authentication.getAuthorities().stream().findFirst()
                    .map(authority -> authority.getAuthority()).orElse(null));
            userRepository.findByEmail(authentication.getName()).ifPresent(user -> log.setUserId(user.getId()));
        }

        String errorMessage = failure == null
                ? (String) request.getAttribute(ERROR_MESSAGE_ATTRIBUTE)
                : safeMessage(failure);
        log.setErrorMessage(trim(errorMessage, 2000));
        log.setDescription(descriptionFor(log, errorMessage));

        try {
            repository.save(log);
        } catch (RuntimeException ignored) {
            // La auditoría no debe cambiar la respuesta de la operación original.
        }
    }

    @Transactional(readOnly = true)
    public AuditLogPageDTO search(LocalDate from, LocalDate to, String user, String action,
                                  String module, Integer status, String text, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        LocalDateTime fromDateTime = from == null ? null : from.atStartOfDay();
        LocalDateTime toDateTime = to == null ? null : to.plusDays(1).atStartOfDay();
        var result = repository.search(fromDateTime, toDateTime, blankToNull(user),
                blankToNull(action), blankToNull(module), status, blankToNull(text),
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "occurredAt")));
        List<AuditLogResponseDTO> items = result.getContent().stream().map(this::toResponse).toList();
        return new AuditLogPageDTO(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void removeExpiredLogs() {
        repository.deleteOlderThan(LocalDateTime.now().minusDays(retentionDays));
    }

    private AuditLogResponseDTO toResponse(AuditLogs log) {
        return new AuditLogResponseDTO(log.getId(), log.getOccurredAt(), log.getUserId(), log.getUserEmail(),
                log.getRole(), log.getAction(), log.getModule(), log.getResourceId(), log.getHttpMethod(),
                log.getEndpoint(), log.getStatusCode(), log.getDescription(), log.getIpAddress(),
                log.getUserAgent(), log.getErrorMessage());
    }

    private String actionFor(String method, int statusCode) {
        if (statusCode >= 400) return "ERROR";
        return switch (method.toUpperCase()) {
            case "POST" -> "CREATE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> "WRITE";
        };
    }

    private String moduleFor(String path) {
        String[] parts = path.split("/");
        if (parts.length < 3) return "system";
        return switch (parts[2]) {
            case "appointments" -> "appointments";
            case "backups" -> "backups";
            case "categories" -> "categories";
            case "clients" -> "clients";
            case "employees" -> "employees";
            case "reports" -> "reports";
            case "sales" -> "sales";
            case "services" -> "services";
            case "users" -> "users";
            default -> parts[2];
        };
    }

    private String resourceIdFor(String path) {
        var matcher = RESOURCE_ID.matcher(path);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String descriptionFor(AuditLogs log, String errorMessage) {
        String module = moduleLabel(log.getModule());
        if (log.getStatusCode() >= 400) {
            return "No se pudo completar la operación en el módulo de " + module + "."
                    + (errorMessage == null || errorMessage.isBlank() ? "" : " Motivo: " + errorMessage);
        }
        return switch (log.getAction()) {
            case "CREATE" -> "Se creó un registro en el módulo de " + module + ".";
            case "UPDATE" -> "Se actualizó un registro en el módulo de " + module + ".";
            case "DELETE" -> "Se eliminó o desactivó un registro en el módulo de " + module + ".";
            default -> "Se realizó una operación en el módulo de " + module + ".";
        };
    }

    private String moduleLabel(String module) {
        return switch (module) {
            case "appointments" -> "citas";
            case "backups" -> "backups";
            case "categories" -> "categorías";
            case "clients" -> "clientes";
            case "employees" -> "empleados";
            case "reports" -> "reportes";
            case "sales" -> "ventas";
            case "services" -> "servicios";
            case "users" -> "usuarios";
            default -> module;
        };
    }

    private String safeMessage(Throwable failure) {
        Throwable current = failure;
        while (current.getCause() != null && (current.getMessage() == null || current.getMessage().isBlank())) {
            current = current.getCause();
        }
        return current.getMessage();
    }

    private String trim(String value, int maxLength) {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
