package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.CashierAppointmentDTO;
import com.glamgest.app.application.dto.sale.CashierGroupDTO;
import com.glamgest.app.common.constant.Constant;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.infrastructure.persistence.entity.Appointments;
import com.glamgest.app.infrastructure.persistence.repository.JpaAppointmentRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaSaleDetailsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class CashierGroupService {

    private final JpaAppointmentRepository appointmentRepository;
    private final JpaSaleDetailsRepository saleDetailsRepository;
    private final ZoneId businessZone;

    public CashierGroupService(JpaAppointmentRepository appointmentRepository,
                               JpaSaleDetailsRepository saleDetailsRepository,
                               @Value("${business.time-zone:America/Bogota}") String businessTimeZone) {
        this.appointmentRepository = appointmentRepository;
        this.saleDetailsRepository = saleDetailsRepository;
        this.businessZone = ZoneId.of(businessTimeZone);
    }

    @Transactional(readOnly = true)
    public CashierGroupDTO getForAppointment(Integer appointmentId) {
        Appointments selected = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + appointmentId));
        ensureEligible(selected);

        Integer clientId = selected.getClientId().getClientId();
        LocalDate businessDate = selected.getAppointmentDatetime().toInstant().atZone(businessZone).toLocalDate();
        List<Appointments> sameClientDay = appointmentRepository.findByClientId_ClientId(clientId).stream()
                .filter(this::isEligible)
                .filter(appointment -> appointment.getAppointmentDatetime().toInstant().atZone(businessZone)
                        .toLocalDate().equals(businessDate))
                .sorted(Comparator.comparing(Appointments::getAppointmentDatetime))
                .toList();

        List<Integer> appointmentIds = sameClientDay.stream().map(Appointments::getAppointmentId).toList();
        Set<Integer> billed = appointmentIds.isEmpty()
                ? Set.of()
                : saleDetailsRepository.findActivelyBilledAppointmentIds(appointmentIds);

        if (billed.contains(appointmentId)) {
            throw new OperationNotAllowedException("La cita ya está incluida en una venta activa.");
        }

        List<CashierAppointmentDTO> lines = sameClientDay.stream()
                .filter(appointment -> !billed.contains(appointment.getAppointmentId()))
                .map(this::toLine)
                .toList();
        return new CashierGroupDTO(clientId, selected.getClientId().getName(), businessDate, lines);
    }

    private void ensureEligible(Appointments appointment) {
        if (!isEligible(appointment)) {
            throw new OperationNotAllowedException("Una cita cancelada o marcada como ausente no puede pasar a caja.");
        }
    }

    private boolean isEligible(Appointments appointment) {
        return !Constant.APPOINTMENT_STATUS_CANCELLED.equalsIgnoreCase(appointment.getStatus())
                && !Constant.APPOINTMENT_STATUS_NO_SHOW.equalsIgnoreCase(appointment.getStatus());
    }

    private CashierAppointmentDTO toLine(Appointments appointment) {
        var service = appointment.getServiceId();
        var employee = appointment.getEmployeeId();
        int duration = appointment.getDurationMinutes() != null
                ? appointment.getDurationMinutes()
                : service.getDurationMinutes();
        return new CashierAppointmentDTO(
                appointment.getAppointmentId(),
                appointment.getAppointmentDatetime().toInstant().atZone(businessZone).toLocalDateTime(),
                appointment.getStatus(),
                service.getServiceId(),
                service.getName(),
                employee.getEmployeeId(),
                employee.getName(),
                duration,
                service.getPrice());
    }
}
