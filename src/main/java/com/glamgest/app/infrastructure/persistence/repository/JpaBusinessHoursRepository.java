package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaBusinessHoursRepository extends JpaRepository<BusinessHours, Long> {
    List<BusinessHours> findAllByOrderByDayOfWeekAsc();
    Optional<BusinessHours> findByDayOfWeek(Integer dayOfWeek);
}
