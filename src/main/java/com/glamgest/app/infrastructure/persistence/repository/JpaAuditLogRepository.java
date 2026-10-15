package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.AuditLogs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogs, Long> {

    @Query("""
            SELECT l FROM AuditLogs l
            WHERE (:from IS NULL OR l.occurredAt >= :from)
              AND (:to IS NULL OR l.occurredAt < :to)
              AND (:user IS NULL OR LOWER(l.userEmail) LIKE LOWER(CONCAT('%', :user, '%')))
              AND (:action IS NULL OR l.action = :action)
              AND (:module IS NULL OR l.module = :module)
              AND (:status IS NULL OR l.statusCode = :status)
              AND (:text IS NULL OR LOWER(l.description) LIKE LOWER(CONCAT('%', :text, '%'))
                   OR LOWER(l.endpoint) LIKE LOWER(CONCAT('%', :text, '%')))
            ORDER BY l.occurredAt DESC
            """)
    Page<AuditLogs> search(@Param("from") LocalDateTime from,
                           @Param("to") LocalDateTime to,
                           @Param("user") String user,
                           @Param("action") String action,
                           @Param("module") String module,
                           @Param("status") Integer status,
                           @Param("text") String text,
                           Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM AuditLogs l WHERE l.occurredAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
