package com.glamgest.app.application.service.schedule;

import com.glamgest.app.application.dto.schedule.BusinessHoursDTO;
import com.glamgest.app.application.dto.schedule.BusinessScheduleExceptionDTO;
import com.glamgest.app.application.dto.schedule.BusinessScheduleExceptionRequestDTO;
import com.glamgest.app.application.dto.schedule.EmployeeAvailableSlotsDTO;
import com.glamgest.app.application.dto.schedule.EmployeeUnavailabilityDTO;
import com.glamgest.app.application.dto.schedule.EmployeeUnavailabilityRequestDTO;
import com.glamgest.app.application.dto.schedule.ScheduleAvailabilityDTO;
import com.glamgest.app.common.constant.Constant;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.exception.ScheduleConflictException;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import com.glamgest.app.domain.model.Appointment;
import com.glamgest.app.domain.model.Employee;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.domain.repository.EmployeeRepository;
import com.glamgest.app.domain.repository.ServiceRepository;
import com.glamgest.app.infrastructure.persistence.entity.BusinessHours;
import com.glamgest.app.infrastructure.persistence.entity.BusinessScheduleException;
import com.glamgest.app.infrastructure.persistence.entity.EmployeeUnavailability;
import com.glamgest.app.infrastructure.persistence.entity.Employees;
import com.glamgest.app.infrastructure.persistence.repository.JpaBusinessHoursRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaBusinessScheduleExceptionRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaEmployeeUnavailabilityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BusinessScheduleService {

    private static final int SLOT_MINUTES = 15;

    private final JpaBusinessHoursRepository hoursRepository;
    private final JpaBusinessScheduleExceptionRepository exceptionRepository;
    private final JpaEmployeeUnavailabilityRepository unavailabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;
    private final ZoneId businessZone;

    public BusinessScheduleService(JpaBusinessHoursRepository hoursRepository,
                                   JpaBusinessScheduleExceptionRepository exceptionRepository,
                                   JpaEmployeeUnavailabilityRepository unavailabilityRepository,
                                   AppointmentRepository appointmentRepository,
                                   EmployeeRepository employeeRepository,
                                   ServiceRepository serviceRepository,
                                   @Value("${business.time-zone:America/Bogota}") String businessTimeZone) {
        this.hoursRepository = hoursRepository;
        this.exceptionRepository = exceptionRepository;
        this.unavailabilityRepository = unavailabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.employeeRepository = employeeRepository;
        this.serviceRepository = serviceRepository;
        this.businessZone = ZoneId.of(businessTimeZone);
    }

    @Transactional
    public List<BusinessHoursDTO> getBusinessHours() {
        ensureDefaultHours();
        return hoursRepository.findAllByOrderByDayOfWeekAsc().stream().map(this::toDto).toList();
    }

    @Transactional
    public List<BusinessHoursDTO> updateBusinessHours(List<BusinessHoursDTO> requestedHours) {
        Map<Integer, BusinessHoursDTO> hoursByDay = validateWeek(requestedHours);
        validateFutureAppointmentsForWeeklyChange(hoursByDay);

        for (BusinessHoursDTO requested : hoursByDay.values()) {
            BusinessHours entity = hoursRepository.findByDayOfWeek(requested.dayOfWeek()).orElseGet(BusinessHours::new);
            entity.setDayOfWeek(requested.dayOfWeek());
            entity.setClosed(requested.closed());
            entity.setOpensAt(requested.closed() ? null : requested.opensAt());
            entity.setClosesAt(requested.closed() ? null : requested.closesAt());
            hoursRepository.save(entity);
        }
        return hoursRepository.findAllByOrderByDayOfWeekAsc().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<BusinessScheduleExceptionDTO> getExceptions(LocalDate from, LocalDate to) {
        return exceptionRepository.findByExceptionDateBetweenOrderByExceptionDateAsc(from, to).stream()
                .map(this::toDto).toList();
    }

    @Transactional
    public BusinessScheduleExceptionDTO saveException(BusinessScheduleExceptionRequestDTO request) {
        validateException(request);
        ensureNoAppointmentsOutsideHours(request.date(), request.closed(), request.opensAt(), request.closesAt());

        BusinessScheduleException entity = exceptionRepository.findByExceptionDate(request.date())
                .orElseGet(BusinessScheduleException::new);
        entity.setExceptionDate(request.date());
        entity.setClosed(request.closed());
        entity.setOpensAt(request.closed() ? null : request.opensAt());
        entity.setClosesAt(request.closed() ? null : request.closesAt());
        entity.setReason(request.reason());
        return toDto(exceptionRepository.save(entity));
    }

    @Transactional
    public void deleteException(Long id) {
        BusinessScheduleException exception = exceptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business schedule exception not found with id " + id));
        ensureNoAppointmentsOutsideHours(exception.getExceptionDate(), null, null, null);
        exceptionRepository.delete(exception);
    }

    @Transactional(readOnly = true)
    public List<EmployeeUnavailabilityDTO> getEmployeeUnavailability(LocalDateTime from, LocalDateTime to) {
        return unavailabilityRepository.findByStartsAtLessThanAndEndsAtGreaterThanOrderByStartsAtAsc(to, from)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public EmployeeUnavailabilityDTO createEmployeeUnavailability(EmployeeUnavailabilityRequestDTO request) {
        if (request.employeeId() == null || request.startsAt() == null || request.endsAt() == null
                || !request.startsAt().isBefore(request.endsAt())) {
            throw new OperationNotAllowedException("El rango de ausencia del empleado no es válido.");
        }
        if (request.startsAt().atZone(businessZone).isBefore(java.time.ZonedDateTime.now(businessZone))) {
            throw new OperationNotAllowedException("El bloqueo del empleado debe comenzar ahora o en una fecha futura.");
        }
        Employee employee = employeeRepository.findByIdIncludingInactive(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + request.employeeId()));
        if (Boolean.FALSE.equals(employee.getActive())) {
            throw new OperationNotAllowedException("No se puede bloquear la agenda de un empleado inactivo.");
        }
        List<Appointment> conflicts = findAppointmentConflicts(request.employeeId(), request.startsAt(), request.endsAt(), null);
        if (!conflicts.isEmpty()) {
            throw new ScheduleConflictException("El empleado tiene " + conflicts.size()
                    + " cita(s) en el período. Reasígnalas o cancélalas antes de bloquearlo.");
        }

        EmployeeUnavailability entity = new EmployeeUnavailability();
        entity.setEmployee(new Employees(employee.getId()));
        entity.setStartsAt(request.startsAt());
        entity.setEndsAt(request.endsAt());
        entity.setReason(trimReason(request.reason()));
        return toDto(unavailabilityRepository.save(entity));
    }

    @Transactional
    public void deleteEmployeeUnavailability(Long id) {
        EmployeeUnavailability block = unavailabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee unavailability not found with id " + id));
        unavailabilityRepository.delete(block);
    }

    @Transactional
    public ScheduleAvailabilityDTO getAvailability(LocalDate date, List<Integer> serviceIds,
                                                  Integer ignoredAppointmentId, Integer clientId) {
        if (date == null || serviceIds == null || serviceIds.isEmpty()) {
            throw new OperationNotAllowedException("La fecha y al menos un servicio son obligatorios para consultar disponibilidad.");
        }
        int duration = 0;
        for (Integer serviceId : serviceIds) {
            var selectedService = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found with id " + serviceId));
            Integer serviceDuration = selectedService.getDurationMinutes();
            if (serviceDuration == null || serviceDuration <= 0 || serviceDuration % SLOT_MINUTES != 0) {
                throw new OperationNotAllowedException("La duración del servicio no es válida para agendar.");
            }
            duration += serviceDuration;
        }

        BusinessHoursDTO hours = hoursFor(date, null);
        if (hours.closed()) {
            return new ScheduleAvailabilityDTO(date, true, null, null, duration, List.of());
        }

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Appointment> clientAppointments = clientId == null
                ? List.of()
                : appointmentRepository.findAllByClientId(clientId);
        List<EmployeeUnavailability> blocks = unavailabilityRepository
                .findByStartsAtLessThanAndEndsAtGreaterThanOrderByStartsAtAsc(dayEnd, dayStart);
        List<EmployeeAvailableSlotsDTO> employeeSlots = new ArrayList<>();
        for (Employee employee : employeeRepository.findAll()) {
            List<Appointment> existing = appointmentRepository.findAllByEmployeeId(employee.getId());
            List<LocalTime> availableStarts = new ArrayList<>();
            for (LocalTime slot = hours.opensAt(); !slot.plusMinutes(duration).isAfter(hours.closesAt()); slot = slot.plusMinutes(SLOT_MINUTES)) {
                LocalDateTime startsAt = LocalDateTime.of(date, slot);
                LocalDateTime endsAt = startsAt.plusMinutes(duration);
                if (startsAt.atZone(businessZone).isBefore(java.time.ZonedDateTime.now(businessZone))) continue;
                if (hasAppointmentOverlap(existing, startsAt, endsAt, ignoredAppointmentId)) continue;
                if (clientId != null && hasAppointmentOverlap(clientAppointments, startsAt, endsAt, ignoredAppointmentId)) continue;
                if (blocks.stream().anyMatch(block -> block.getEmployee().getEmployeeId().equals(employee.getId())
                        && overlaps(startsAt, endsAt, block.getStartsAt(), block.getEndsAt()))) continue;
                availableStarts.add(slot);
            }
            employeeSlots.add(new EmployeeAvailableSlotsDTO(employee.getId(), employee.getName(), availableStarts));
        }
        return new ScheduleAvailabilityDTO(date, false, hours.opensAt(), hours.closesAt(), duration, employeeSlots);
    }

    @Transactional
    public void validateAppointment(Date appointmentDate, Integer clientId, Integer employeeId,
                                    Integer durationMinutes, Integer ignoredAppointmentId) {
        LocalDateTime startsAt = appointmentDate.toInstant().atZone(businessZone).toLocalDateTime();
        LocalTime startTime = startsAt.toLocalTime();
        if (startTime.getMinute() % SLOT_MINUTES != 0 || startTime.getSecond() != 0 || startTime.getNano() != 0) {
            throw new ScheduleConflictException("La cita debe iniciar en un intervalo de 15 minutos.");
        }
        LocalDateTime endsAt = startsAt.plusMinutes(durationMinutes);
        BusinessHoursDTO hours = hoursFor(startsAt.toLocalDate(), null);
        if (hours.closed() || startsAt.toLocalTime().isBefore(hours.opensAt())
                || endsAt.toLocalTime().isAfter(hours.closesAt())
                || !endsAt.toLocalDate().equals(startsAt.toLocalDate())) {
            throw new ScheduleConflictException("La cita queda fuera del horario de atención del negocio.");
        }
        List<EmployeeUnavailability> blocks = unavailabilityRepository
                .findByEmployee_EmployeeIdAndStartsAtLessThanAndEndsAtGreaterThan(employeeId, endsAt, startsAt);
        if (!blocks.isEmpty()) {
            throw new ScheduleConflictException("El empleado no está disponible en ese horario.");
        }
        if (hasAppointmentOverlap(appointmentRepository.findAllByEmployeeId(employeeId), startsAt, endsAt,
                ignoredAppointmentId)) {
            throw new ScheduleConflictException("El empleado ya tiene una cita en ese horario.");
        }
        if (clientId != null && hasAppointmentOverlap(appointmentRepository.findAllByClientId(clientId),
                startsAt, endsAt, ignoredAppointmentId)) {
            throw new ScheduleConflictException("El cliente ya tiene otra cita en ese horario.");
        }
    }

    private Map<Integer, BusinessHoursDTO> validateWeek(List<BusinessHoursDTO> requestedHours) {
        if (requestedHours == null || requestedHours.size() != 7) {
            throw new OperationNotAllowedException("Debes configurar los siete días de la semana.");
        }
        Map<Integer, BusinessHoursDTO> result = new HashMap<>();
        for (BusinessHoursDTO day : requestedHours) {
            if (day.dayOfWeek() == null || day.dayOfWeek() < 1 || day.dayOfWeek() > 7
                    || result.containsKey(day.dayOfWeek())) {
                throw new OperationNotAllowedException("La configuración semanal contiene un día inválido o repetido.");
            }
            if (!day.closed()) validateRange(day.opensAt(), day.closesAt());
            result.put(day.dayOfWeek(), day);
        }
        return result;
    }

    private void validateException(BusinessScheduleExceptionRequestDTO request) {
        if (request.date() == null || request.date().isBefore(LocalDate.now(businessZone))) {
            throw new OperationNotAllowedException("La fecha excepcional debe ser hoy o futura.");
        }
        if (!request.closed()) validateRange(request.opensAt(), request.closesAt());
    }

    private void validateRange(LocalTime opensAt, LocalTime closesAt) {
        if (opensAt == null || closesAt == null || !opensAt.isBefore(closesAt)
                || opensAt.getMinute() % SLOT_MINUTES != 0 || closesAt.getMinute() % SLOT_MINUTES != 0) {
            throw new OperationNotAllowedException("El horario debe tener inicio y fin válidos en intervalos de 15 minutos.");
        }
    }

    private void validateFutureAppointmentsForWeeklyChange(Map<Integer, BusinessHoursDTO> proposedHours) {
        Map<LocalDate, BusinessScheduleException> exceptions = new HashMap<>();
        exceptionRepository.findAll().forEach(item -> exceptions.put(item.getExceptionDate(), item));
        for (Appointment appointment : appointmentRepository.findAll()) {
            if (!isSchedulable(appointment) || !isFuture(appointment)) continue;
            LocalDateTime start = toLocal(appointment.getAppointmentDatetime());
            int duration = appointmentDuration(appointment);
            BusinessScheduleException exception = exceptions.get(start.toLocalDate());
            BusinessHoursDTO weekly = proposedHours.get(start.getDayOfWeek().getValue());
            if (!fitsHours(start, duration, exception, weekly)) {
                throw new ScheduleConflictException("El nuevo horario deja citas ya agendadas fuera del horario del negocio.");
            }
        }
    }

    private void ensureNoAppointmentsOutsideHours(LocalDate date, Boolean exceptionClosed,
                                                 LocalTime exceptionOpens, LocalTime exceptionCloses) {
        BusinessHoursDTO weekly = weeklyHoursFor(date);
        BusinessScheduleException override = null;
        if (exceptionClosed != null) {
            override = new BusinessScheduleException();
            override.setExceptionDate(date);
            override.setClosed(exceptionClosed);
            override.setOpensAt(exceptionOpens);
            override.setClosesAt(exceptionCloses);
        }
        for (Appointment appointment : appointmentRepository.findAll()) {
            if (!isSchedulable(appointment) || !toLocal(appointment.getAppointmentDatetime()).toLocalDate().equals(date)) continue;
            if (!fitsHours(toLocal(appointment.getAppointmentDatetime()), appointmentDuration(appointment), override, weekly)) {
                throw new ScheduleConflictException("Hay citas agendadas en el horario que intentas bloquear o cambiar.");
            }
        }
    }

    private boolean fitsHours(LocalDateTime startsAt, int duration, BusinessScheduleException exception,
                              BusinessHoursDTO weekly) {
        boolean closed = exception == null ? weekly.closed() : Boolean.TRUE.equals(exception.getClosed());
        LocalTime opensAt = exception == null ? weekly.opensAt() : exception.getOpensAt();
        LocalTime closesAt = exception == null ? weekly.closesAt() : exception.getClosesAt();
        if (closed || opensAt == null || closesAt == null) return false;
        LocalDateTime endsAt = startsAt.plusMinutes(duration);
        return !startsAt.toLocalTime().isBefore(opensAt)
                && !endsAt.toLocalTime().isAfter(closesAt)
                && endsAt.toLocalDate().equals(startsAt.toLocalDate());
    }

    private BusinessHoursDTO hoursFor(LocalDate date, Map<Integer, BusinessHoursDTO> proposedHours) {
        BusinessScheduleException exception = exceptionRepository.findByExceptionDate(date).orElse(null);
        if (exception != null) {
            return new BusinessHoursDTO(date.getDayOfWeek().getValue(), Boolean.TRUE.equals(exception.getClosed()),
                    exception.getOpensAt(), exception.getClosesAt());
        }
        if (proposedHours != null) return proposedHours.get(date.getDayOfWeek().getValue());
        return proposedHours == null
                ? weeklyHoursFor(date)
                : proposedHours.get(date.getDayOfWeek().getValue());
    }

    private BusinessHoursDTO weeklyHoursFor(LocalDate date) {
        ensureDefaultHours();
        BusinessHours entity = hoursRepository.findByDayOfWeek(date.getDayOfWeek().getValue()).orElseThrow();
        return toDto(entity);
    }

    private void ensureDefaultHours() {
        if (hoursRepository.count() > 0) return;
        for (int day = 1; day <= 7; day++) {
            BusinessHours hours = new BusinessHours();
            hours.setDayOfWeek(day);
            boolean closed = day == DayOfWeek.SUNDAY.getValue();
            hours.setClosed(closed);
            hours.setOpensAt(closed ? null : LocalTime.of(9, 0));
            hours.setClosesAt(closed ? null : LocalTime.of(19, 0));
            hoursRepository.save(hours);
        }
    }

    private int appointmentDuration(Appointment appointment) {
        if (appointment.getDurationMinutes() != null) return appointment.getDurationMinutes();
        return serviceRepository.findByIdIncludingInactive(appointment.getServiceId())
                .map(service -> service.getDurationMinutes() == null ? SLOT_MINUTES : service.getDurationMinutes())
                .orElse(SLOT_MINUTES);
    }

    private LocalDateTime toLocal(Date date) {
        return date.toInstant().atZone(businessZone).toLocalDateTime();
    }

    private boolean isFuture(Appointment appointment) {
        return appointment.getAppointmentDatetime().after(Date.from(java.time.ZonedDateTime.now(businessZone).toInstant()));
    }

    private boolean isSchedulable(Appointment appointment) {
        return !Constant.APPOINTMENT_STATUS_CANCELLED.equalsIgnoreCase(appointment.getStatus())
                && !Constant.APPOINTMENT_STATUS_NO_SHOW.equalsIgnoreCase(appointment.getStatus());
    }

    private boolean hasAppointmentOverlap(List<Appointment> appointments, LocalDateTime startsAt,
                                          LocalDateTime endsAt, Integer ignoredAppointmentId) {
        for (Appointment appointment : appointments) {
            if (ignoredAppointmentId != null && ignoredAppointmentId.equals(appointment.getId())) continue;
            if (!isSchedulable(appointment)) continue;
            LocalDateTime existingStart = toLocal(appointment.getAppointmentDatetime());
            LocalDateTime existingEnd = existingStart.plusMinutes(appointmentDuration(appointment));
            if (overlaps(startsAt, endsAt, existingStart, existingEnd)) return true;
        }
        return false;
    }

    private List<Appointment> findAppointmentConflicts(Integer employeeId, LocalDateTime startsAt,
                                                        LocalDateTime endsAt, Integer ignoredAppointmentId) {
        return appointmentRepository.findAllByEmployeeId(employeeId).stream()
                .filter(appointment -> ignoredAppointmentId == null || !ignoredAppointmentId.equals(appointment.getId()))
                .filter(this::isSchedulable)
                .filter(appointment -> {
                    LocalDateTime existingStart = toLocal(appointment.getAppointmentDatetime());
                    LocalDateTime existingEnd = existingStart.plusMinutes(appointmentDuration(appointment));
                    return overlaps(startsAt, endsAt, existingStart, existingEnd);
                })
                .toList();
    }

    private boolean overlaps(LocalDateTime firstStart, LocalDateTime firstEnd,
                             LocalDateTime secondStart, LocalDateTime secondEnd) {
        return firstStart.isBefore(secondEnd) && firstEnd.isAfter(secondStart);
    }

    private BusinessHoursDTO toDto(BusinessHours entity) {
        return new BusinessHoursDTO(entity.getDayOfWeek(), Boolean.TRUE.equals(entity.getClosed()),
                entity.getOpensAt(), entity.getClosesAt());
    }

    private BusinessScheduleExceptionDTO toDto(BusinessScheduleException entity) {
        return new BusinessScheduleExceptionDTO(entity.getId(), entity.getExceptionDate(),
                Boolean.TRUE.equals(entity.getClosed()), entity.getOpensAt(), entity.getClosesAt(), entity.getReason());
    }

    private EmployeeUnavailabilityDTO toDto(EmployeeUnavailability entity) {
        return new EmployeeUnavailabilityDTO(entity.getId(), entity.getEmployee().getEmployeeId(),
                entity.getEmployee().getName(), entity.getStartsAt(), entity.getEndsAt(), entity.getReason());
    }

    private String trimReason(String reason) {
        if (reason == null || reason.isBlank()) return null;
        String value = reason.trim();
        return value.length() <= 200 ? value : value.substring(0, 200);
    }
}
