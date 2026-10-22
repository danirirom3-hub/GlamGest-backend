package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.EmployeeUnavailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaEmployeeUnavailabilityRepository extends JpaRepository<EmployeeUnavailability, Long> {
    List<EmployeeUnavailability> findByStartsAtLessThanAndEndsAtGreaterThanOrderByStartsAtAsc(
            LocalDateTime rangeEnd, LocalDateTime rangeStart);

    List<EmployeeUnavailability> findByEmployee_EmployeeIdAndStartsAtLessThanAndEndsAtGreaterThan(
            Integer employeeId, LocalDateTime rangeEnd, LocalDateTime rangeStart);
}
