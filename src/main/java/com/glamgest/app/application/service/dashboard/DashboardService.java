package com.glamgest.app.application.service.dashboard;

import com.glamgest.app.application.dto.dashboard.DashboardCountDTO;
import com.glamgest.app.application.dto.dashboard.DashboardEmployeeMetricDTO;
import com.glamgest.app.application.dto.dashboard.DashboardRevenuePointDTO;
import com.glamgest.app.application.dto.dashboard.DashboardServiceMetricDTO;
import com.glamgest.app.application.dto.dashboard.DashboardSummaryDTO;
import com.glamgest.app.common.exception.InvalidDashboardPeriodException;
import com.glamgest.app.infrastructure.persistence.repository.JpaAppointmentRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaClientRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaSaleDetailsRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaSalesRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final JpaSalesRepository salesRepository;
    private final JpaAppointmentRepository appointmentRepository;
    private final JpaClientRepository clientRepository;
    private final JpaSaleDetailsRepository saleDetailsRepository;

    public DashboardService(JpaSalesRepository salesRepository,
                            JpaAppointmentRepository appointmentRepository,
                            JpaClientRepository clientRepository,
                            JpaSaleDetailsRepository saleDetailsRepository) {
        this.salesRepository = salesRepository;
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.saleDetailsRepository = saleDetailsRepository;
    }

    public DashboardSummaryDTO summary(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        long totalSales = salesRepository.countActiveBetween(period.from(), period.to());
        long totalRevenue = salesRepository.sumActiveBetween(period.from(), period.to());
        BigDecimal averageTicket = totalSales == 0
                ? BigDecimal.ZERO.setScale(2)
                : BigDecimal.valueOf(totalRevenue)
                        .divide(BigDecimal.valueOf(totalSales), 2, RoundingMode.HALF_UP);

        List<DashboardCountDTO> appointmentStatuses = appointmentRepository
                .countByStatusBetween(period.from(), period.to()).stream()
                .map(row -> new DashboardCountDTO(row[0] == null ? "UNKNOWN" : row[0].toString(), number(row[1])))
                .toList();

        return new DashboardSummaryDTO(
                period.fromDate(),
                period.toDate(),
                totalRevenue,
                totalSales,
                averageTicket,
                clientRepository.countRegisteredBetween(period.from(), period.to()),
                appointmentRepository.countBetween(period.from(), period.to()),
                appointmentStatuses);
    }

    public List<DashboardRevenuePointDTO> revenue(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        return salesRepository.revenueByDay(period.from(), period.to()).stream()
                .map(row -> new DashboardRevenuePointDTO(
                        localDate(row[0]), number(row[1]), number(row[2])))
                .toList();
    }

    public List<DashboardCountDTO> appointments(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        return appointmentRepository.countByStatusBetween(period.from(), period.to()).stream()
                .map(row -> new DashboardCountDTO(row[0] == null ? "UNKNOWN" : row[0].toString(), number(row[1])))
                .toList();
    }

    public List<DashboardServiceMetricDTO> services(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        return saleDetailsRepository.revenueByServiceBetween(period.from(), period.to()).stream()
                .map(row -> new DashboardServiceMetricDTO(
                        integer(row[0]), String.valueOf(row[1]), number(row[2]), number(row[3])))
                .toList();
    }

    public List<DashboardEmployeeMetricDTO> employees(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        Map<Integer, EmployeeMetric> metrics = new LinkedHashMap<>();

        appointmentRepository.countByEmployeeBetween(period.from(), period.to()).forEach(row -> {
            Integer id = integer(row[0]);
            metrics.put(id, new EmployeeMetric(String.valueOf(row[1]), number(row[2]), 0));
        });
        saleDetailsRepository.revenueByEmployeeBetween(period.from(), period.to()).forEach(row -> {
            Integer id = integer(row[0]);
            EmployeeMetric current = metrics.get(id);
            metrics.put(id, new EmployeeMetric(String.valueOf(row[1]),
                    current == null ? 0 : current.appointments(), number(row[2])));
        });

        return metrics.entrySet().stream()
                .map(entry -> new DashboardEmployeeMetricDTO(entry.getKey(), entry.getValue().name(),
                        entry.getValue().appointments(), entry.getValue().revenue()))
                .toList();
    }

    private Period period(LocalDate from, LocalDate to) {
        LocalDate resolvedFrom = from == null ? LocalDate.now().withDayOfMonth(1) : from;
        LocalDate resolvedTo = to == null ? LocalDate.now() : to;
        if (resolvedTo.isBefore(resolvedFrom)) {
            throw new InvalidDashboardPeriodException("La fecha final no puede ser anterior a la fecha inicial");
        }
        return new Period(resolvedFrom.atStartOfDay(), resolvedTo.plusDays(1).atStartOfDay(),
                resolvedFrom, resolvedTo);
    }

    private static long number(Object value) {
        return value == null ? 0 : ((Number) value).longValue();
    }

    private static Integer integer(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private static LocalDate localDate(Object value) {
        if (value instanceof Date date) {
            return date.toLocalDate();
        }
        if (value instanceof java.util.Date date) {
            return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }

    private record Period(LocalDateTime from, LocalDateTime to, LocalDate fromDate, LocalDate toDate) {
    }

    private record EmployeeMetric(String name, long appointments, long revenue) {
    }
}
