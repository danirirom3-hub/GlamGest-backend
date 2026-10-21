package com.glamgest.app.application.service.audit;

import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.EmployeeRepository;
import com.glamgest.app.domain.repository.SaleRepository;
import com.glamgest.app.domain.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogResourceNameResolver {

    private final AppointmentRepository appointmentRepository;
    private final SaleRepository saleRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;

    public AuditLogResourceNameResolver(AppointmentRepository appointmentRepository,
                                        SaleRepository saleRepository,
                                        ClientRepository clientRepository,
                                        EmployeeRepository employeeRepository,
                                        ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional(readOnly = true)
    public String appointment(Integer id) {
        var appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) return resourceId(id);

        List<String> parts = new ArrayList<>();
        parts.add(resourceId(id));
        clientRepository.findByIdIncludingInactive(appointment.getClientId())
                .map(client -> client.getName())
                .filter(this::hasText)
                .ifPresent(parts::add);
        serviceRepository.findByIdIncludingInactive(appointment.getServiceId())
                .map(service -> service.getName())
                .filter(this::hasText)
                .ifPresent(parts::add);
        employeeRepository.findByIdIncludingInactive(appointment.getEmployeeId())
                .map(employee -> employee.getName())
                .filter(this::hasText)
                .ifPresent(parts::add);
        return String.join(" · ", parts);
    }

    @Transactional(readOnly = true)
    public String sale(Integer id) {
        var sale = saleRepository.findById(id).orElse(null);
        if (sale == null) return resourceId(id);

        List<String> parts = new ArrayList<>();
        parts.add(resourceId(id));
        clientRepository.findByIdIncludingInactive(sale.getClientId())
                .map(client -> client.getName())
                .filter(this::hasText)
                .ifPresent(parts::add);

        if (sale.getSaleDetails() != null) {
            sale.getSaleDetails().stream()
                    .map(detail -> detail.getServiceId() == null
                            ? null
                            : serviceRepository.findByIdIncludingInactive(detail.getServiceId())
                                    .map(service -> service.getName()).orElse(null))
                    .filter(this::hasText)
                    .distinct()
                    .forEach(parts::add);
        }
        return String.join(" · ", parts);
    }

    private String resourceId(Integer id) {
        return "#" + id;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
