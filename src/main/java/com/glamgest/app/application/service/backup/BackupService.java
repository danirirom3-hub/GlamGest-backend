package com.glamgest.app.application.service.backup;

import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class BackupService {

    private static final Pattern MYSQL_URL = Pattern.compile(
            "jdbc:mysql://([^/:]+)(?::(\\d+))?/([^?]+)");
    private static final String FILE_SUFFIX = ".sql.gz";

    private final Path directory;
    private final String dumpCommand;
    private final String restoreCommand;
    private final String datasourceUrl;
    private final String datasourceUsername;
    private final String datasourcePassword;
    private final UserRepository userRepository;

    public BackupService(
            @Value("${backup.directory:backups}") String directory,
            @Value("${backup.mysqldump-command:mysqldump}") String dumpCommand,
            @Value("${backup.mysql-command:mysql}") String restoreCommand,
            @Value("${spring.datasource.url}") String datasourceUrl,
            @Value("${spring.datasource.username}") String datasourceUsername,
            @Value("${spring.datasource.password}") String datasourcePassword,
            UserRepository userRepository) {
        this.directory = Path.of(directory).toAbsolutePath().normalize();
        this.dumpCommand = dumpCommand;
        this.restoreCommand = restoreCommand;
        this.datasourceUrl = datasourceUrl;
        this.datasourceUsername = datasourceUsername;
        this.datasourcePassword = datasourcePassword;
        this.userRepository = userRepository;
    }

    public synchronized BackupInfo createBackup() {
        DatabaseConnection database = databaseConnection();
        String id = UUID.randomUUID().toString();
        Instant createdAt = Instant.now();
        Path file = backupFile(id);
        try {
            Files.createDirectories(directory);
            ProcessBuilder builder = new ProcessBuilder(dumpCommand,
                    "--single-transaction", "--routines", "--triggers", "--events",
                    "--hex-blob",
                    "-h", database.host(), "-P", database.port(), "-u", datasourceUsername,
                    database.name())
                    .redirectErrorStream(false);
            builder.environment().put("MYSQL_PWD", datasourcePassword);
            Process process = builder.start();

            try (InputStream input = process.getInputStream();
                 OutputStream output = new GZIPOutputStream(Files.newOutputStream(file,
                         StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE))) {
                input.transferTo(output);
            }
            String error = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            if (process.waitFor() != 0) {
                Files.deleteIfExists(file);
                throw new IllegalStateException("No se pudo crear el backup: " + safeProcessError(error));
            }

            BackupInfo info = new BackupInfo(id, createdAt, Files.size(file), sha256(file), "COMPLETED");
            writeMetadata(info);
            return info;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            deleteBackupFiles(id);
            throw new IllegalStateException("La creación del backup fue interrumpida", exception);
        } catch (IOException exception) {
            deleteBackupFiles(id);
            throw new IllegalStateException("No se pudo crear el backup", exception);
        }
    }

    public synchronized List<BackupInfo> listBackups() {
        try {
            Files.createDirectories(directory);
            return Files.list(directory)
                    .filter(path -> path.getFileName().toString().endsWith(FILE_SUFFIX))
                    .map(this::readMetadata)
                    .sorted(Comparator.comparing(BackupInfo::createdAt).reversed())
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo consultar el historial de backups", exception);
        }
    }

    public Path backupPath(String id) {
        validateId(id);
        Path path = backupFile(id);
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("El backup no existe");
        }
        return path;
    }

    public synchronized BackupInfo restore(String id) {
        Path source = backupPath(id);
        BackupInfo sourceInfo = readMetadata(source);
        try {
            if (!sourceInfo.sha256().equals(sha256(source))) {
                throw new IllegalStateException("El checksum del backup no coincide con sus metadatos");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo validar el backup", exception);
        }
        createBackup();
        DatabaseConnection database = databaseConnection();
        try {
            ProcessBuilder builder = new ProcessBuilder(restoreCommand,
                    "-h", database.host(), "-P", database.port(), "-u", datasourceUsername,
                    database.name())
                    .redirectErrorStream(false);
            builder.environment().put("MYSQL_PWD", datasourcePassword);
            Process process = builder.start();
            try (InputStream input = new GZIPInputStream(Files.newInputStream(source));
                 OutputStream output = process.getOutputStream()) {
                input.transferTo(output);
            }
            String error = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            if (process.waitFor() != 0) {
                throw new IllegalStateException("No se pudo restaurar el backup: " + safeProcessError(error));
            }
            userRepository.clearAllActiveSessions();
            return sourceInfo;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La restauración fue interrumpida", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo restaurar el backup", exception);
        }
    }

    public synchronized void delete(String id) {
        validateId(id);
        deleteBackupFiles(id);
    }

    private DatabaseConnection databaseConnection() {
        Matcher matcher = MYSQL_URL.matcher(datasourceUrl);
        if (!matcher.find()) {
            throw new IllegalStateException("La URL de datasource no es una URL MySQL válida");
        }
        return new DatabaseConnection(matcher.group(1),
                matcher.group(2) == null ? "3306" : matcher.group(2), matcher.group(3));
    }

    private BackupInfo readMetadata(Path backup) {
        String id = backup.getFileName().toString().replace(FILE_SUFFIX, "");
        Path metadata = metadataFile(id);
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(metadata)) {
            properties.load(input);
            return new BackupInfo(id, Instant.parse(properties.getProperty("createdAt")),
                    Long.parseLong(properties.getProperty("size")), properties.getProperty("sha256"),
                    properties.getProperty("status", "COMPLETED"));
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Metadatos inválidos para el backup " + id, exception);
        }
    }

    private void writeMetadata(BackupInfo info) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("createdAt", info.createdAt().toString());
        properties.setProperty("size", String.valueOf(info.size()));
        properties.setProperty("sha256", info.sha256());
        properties.setProperty("status", info.status());
        try (OutputStream output = Files.newOutputStream(metadataFile(info.id()), StandardOpenOption.CREATE_NEW)) {
            properties.store(output, "GlamGest backup metadata");
        }
    }

    private String sha256(Path file) throws IOException {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file));
            StringBuilder result = new StringBuilder();
            for (byte value : digest) result.append(String.format("%02x", value));
            return result.toString();
        } catch (Exception exception) {
            throw new IOException("No se pudo calcular el checksum", exception);
        }
    }

    private Path backupFile(String id) { return directory.resolve(id + FILE_SUFFIX); }

    private Path metadataFile(String id) { return directory.resolve(id + ".properties"); }

    private void validateId(String id) {
        try { UUID.fromString(id); } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Identificador de backup inválido");
        }
    }

    private void deleteBackupFiles(String id) {
        try {
            Files.deleteIfExists(backupFile(id));
            Files.deleteIfExists(metadataFile(id));
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo limpiar el backup", exception);
        }
    }

    private String safeProcessError(String error) {
        return error == null || error.isBlank() ? "error desconocido" : error.trim().replaceAll("\\s+", " ");
    }

    public record BackupInfo(String id, Instant createdAt, long size, String sha256, String status) {}

    private record DatabaseConnection(String host, String port, String name) {}
}
