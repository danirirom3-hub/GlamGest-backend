package com.glamgest.app.application.service.audit;

import com.glamgest.app.domain.model.Appointment;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.model.Employee;
import com.glamgest.app.domain.model.Sale;
import com.glamgest.app.domain.model.SaleDetail;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.EmployeeRepository;
import com.glamgest.app.domain.repository.SaleRepository;
import com.glamgest.app.domain.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogResourceNameResolverTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ServiceRepository serviceRepository;

    private AuditLogResourceNameResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new AuditLogResourceNameResolver(appointmentRepository, saleRepository,
                clientRepository, employeeRepository, serviceRepository);
    }

    @Test
    void describesAppointmentsUsingTheirClientServiceAndEmployee() {
        when(appointmentRepository.findById(14)).thenReturn(Optional.of(
                new Appointment(14, new Date(), "PENDING", null, 1, 2, 3, 4)));
        when(clientRepository.findByIdIncludingInactive(1)).thenReturn(Optional.of(
                new Client(1, "Ana López", "ana@example.com", "3000000000", new Date())));
        when(serviceRepository.findByIdIncludingInactive(3)).thenReturn(Optional.of(
                new Service(3, "Corte de hombre", "", 20000, 30, true)));
        when(employeeRepository.findByIdIncludingInactive(2)).thenReturn(Optional.of(
                new Employee(2, "Luis", "3000000001", true)));

        assertEquals("#14 · Ana López · Corte de hombre · Luis", resolver.appointment(14));
    }

    @Test
    void describesSalesUsingTheirClientAndDistinctServices() {
        Sale sale = new Sale(8, new Date(), 40000, "CASH", 1, 2);
        sale.setSaleDetails(List.of(
                new SaleDetail(1, 8, null, 3, 4, 1, 20000, 20000),
                new SaleDetail(2, 8, null, 3, 4, 1, 20000, 20000)));
        when(saleRepository.findById(8)).thenReturn(Optional.of(sale));
        when(clientRepository.findByIdIncludingInactive(1)).thenReturn(Optional.of(
                new Client(1, "Ana López", "ana@example.com", "3000000000", new Date())));
        when(serviceRepository.findByIdIncludingInactive(4)).thenReturn(Optional.of(
                new Service(4, "Corte de hombre", "", 20000, 30, true)));

        assertEquals("#8 · Ana López · Corte de hombre", resolver.sale(8));
    }
}
