package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.BusinessScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaBusinessScheduleExceptionRepository extends JpaRepository<BusinessScheduleException, Long> {
    Optional<BusinessScheduleException> findByExceptionDate(LocalDate exceptionDate);
    List<BusinessScheduleException> findByExceptionDateBetweenOrderByExceptionDateAsc(LocalDate from, LocalDate to);
}
