package com.glamgest.app.application.service.schedule;

import com.glamgest.app.domain.model.Employee;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.domain.repository.EmployeeRepository;
import com.glamgest.app.domain.repository.ServiceRepository;
import com.glamgest.app.common.exception.ScheduleConflictException;
import com.glamgest.app.infrastructure.persistence.entity.BusinessHours;
import com.glamgest.app.infrastructure.persistence.repository.JpaBusinessHoursRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaBusinessScheduleExceptionRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaEmployeeUnavailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessScheduleServiceTest {

    @Mock private JpaBusinessHoursRepository hoursRepository;
    @Mock private JpaBusinessScheduleExceptionRepository exceptionRepository;
    @Mock private JpaEmployeeUnavailabilityRepository unavailabilityRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private ServiceRepository serviceRepository;

    private BusinessScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        scheduleService = new BusinessScheduleService(hoursRepository, exceptionRepository, unavailabilityRepository,
                appointmentRepository, employeeRepository, serviceRepository, "America/Bogota");
    }

    @Test
    void returnsQuarterHourAvailabilityThatFitsTheFullServiceDuration() {
        LocalDate date = LocalDate.now(ZoneId.of("America/Bogota")).plusDays(10);
        BusinessHours hours = hours(date, LocalTime.of(9, 0), LocalTime.of(10, 0), false);
        when(hoursRepository.count()).thenReturn(7L);
        when(hoursRepository.findByDayOfWeek(date.getDayOfWeek().getValue())).thenReturn(Optional.of(hours));
        when(exceptionRepository.findByExceptionDate(date)).thenReturn(Optional.empty());
        when(serviceRepository.findById(4)).thenReturn(Optional.of(
                new Service(4, "Corte", "", 20000, 30, true)));
        when(employeeRepository.findAll()).thenReturn(List.of(new Employee(2, "Luis", "3000000000", true)));
        when(appointmentRepository.findAllByEmployeeId(2)).thenReturn(List.of());
        when(unavailabilityRepository.findByStartsAtLessThanAndEndsAtGreaterThanOrderByStartsAtAsc(
                date.plusDays(1).atStartOfDay(), date.atStartOfDay())).thenReturn(List.of());

        var availability = scheduleService.getAvailability(date, List.of(4), null, null);

        assertEquals(List.of(LocalTime.of(9, 0), LocalTime.of(9, 15), LocalTime.of(9, 30)),
                availability.employees().get(0).startsAt());
    }

    @Test
    void rejectsAppointmentsOutsideConfiguredBusinessHours() {
        LocalDate date = LocalDate.now(ZoneId.of("America/Bogota")).plusDays(10);
        BusinessHours hours = hours(date, LocalTime.of(9, 0), LocalTime.of(19, 0), false);
        when(exceptionRepository.findByExceptionDate(date)).thenReturn(Optional.empty());
        when(hoursRepository.count()).thenReturn(7L);
        when(hoursRepository.findByDayOfWeek(date.getDayOfWeek().getValue())).thenReturn(Optional.of(hours));

        Date afterClosing = Date.from(date.atTime(19, 0).atZone(ZoneId.of("America/Bogota")).toInstant());
        assertThrows(ScheduleConflictException.class, () -> scheduleService.validateAppointment(afterClosing, 1, 2, 30, null));
    }

    @Test
    void rejectsAppointmentsThatDoNotStartOnAQuarterHour() {
        LocalDate date = LocalDate.now(ZoneId.of("America/Bogota")).plusDays(10);
        Date offGridStart = Date.from(date.atTime(10, 5).atZone(ZoneId.of("America/Bogota")).toInstant());

        assertThrows(ScheduleConflictException.class,
                () -> scheduleService.validateAppointment(offGridStart, 1, 2, 30, null));
    }

    private BusinessHours hours(LocalDate date, LocalTime open, LocalTime close, boolean closed) {
        BusinessHours hours = new BusinessHours();
        hours.setDayOfWeek(date.getDayOfWeek().getValue());
        hours.setOpensAt(open);
        hours.setClosesAt(close);
        hours.setClosed(closed);
        return hours;
    }
}
