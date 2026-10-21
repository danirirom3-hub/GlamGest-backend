package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.service.backup.BackupService;
import com.glamgest.app.infrastructure.configuration.AuditLogContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/backups")
@PreAuthorize("hasAuthority('ADMIN')")
public class BackupController {

    private final BackupService backupService;
    private final AuditLogContext auditLogContext;

    public BackupController(BackupService backupService, AuditLogContext auditLogContext) {
        this.backupService = backupService;
        this.auditLogContext = auditLogContext;
    }

    @PostMapping
    public ResponseEntity<BackupService.BackupInfo> create() {
        BackupService.BackupInfo backup = backupService.createBackup();
        auditLogContext.setResource(backup.id(), backup.id());
        return ResponseEntity.ok(backup);
    }

    @GetMapping
    public ResponseEntity<List<BackupService.BackupInfo>> list() {
        return ResponseEntity.ok(backupService.listBackups());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable String id) {
        Path path = backupService.backupPath(id);
        Resource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/gzip"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(path.getFileName().toString()).build().toString())
                .body(resource);
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<BackupService.BackupInfo> restore(@PathVariable String id) {
        BackupService.BackupInfo backup = backupService.restore(id);
        auditLogContext.setResource(backup.id(), backup.id());
        return ResponseEntity.ok(backup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        backupService.delete(id);
        auditLogContext.setResource(id, id);
        return ResponseEntity.noContent().build();
    }
}
