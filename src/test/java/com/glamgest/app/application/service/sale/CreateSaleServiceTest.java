package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.SaleDetailRequestDTO;
import com.glamgest.app.application.dto.sale.SaleRequestDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.infrastructure.persistence.entity.Clients;
import com.glamgest.app.infrastructure.persistence.entity.Employees;
import com.glamgest.app.infrastructure.persistence.entity.SaleDetails;
import com.glamgest.app.infrastructure.persistence.entity.Sales;
import com.glamgest.app.infrastructure.persistence.entity.Services;
import com.glamgest.app.infrastructure.persistence.entity.Users;
import com.glamgest.app.infrastructure.persistence.repository.JpaAppointmentRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaClientRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaEmployeeRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaSalesRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaServiceRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateSaleServiceTest {

    @Test
    void execute_whenSaleIsValid_returnsSaleResponse() {
        JpaSalesRepository jpaSalesRepository = mock(JpaSalesRepository.class);
        JpaClientRepository jpaClientRepository = mock(JpaClientRepository.class);
        JpaUserRepository jpaUserRepository = mock(JpaUserRepository.class);
        JpaAppointmentRepository jpaAppointmentRepository = mock(JpaAppointmentRepository.class);
        JpaEmployeeRepository jpaEmployeeRepository = mock(JpaEmployeeRepository.class);
        JpaServiceRepository jpaServiceRepository = mock(JpaServiceRepository.class);

        CreateSaleService service = new CreateSaleService(
                jpaSalesRepository,
                jpaClientRepository,
                jpaUserRepository,
                jpaAppointmentRepository,
                jpaEmployeeRepository,
                jpaServiceRepository
        );

        SaleRequestDTO request = new SaleRequestDTO();
        request.setClientId(1);
        request.setUserId(2);
        request.setPaymentType("CARD");

        SaleDetailRequestDTO detailRequest = new SaleDetailRequestDTO();
        detailRequest.setServiceId(3);
        detailRequest.setEmployeeId(4);
        detailRequest.setQuantity(2);
<<<<<<< HEAD
        detailRequest.setUnitPrice(7500);
=======
        detailRequest.setUnitPrice(5000);
>>>>>>> 51408e4fe9d7e969b11538bbc8c522b9d34e13aa

        request.setSaleDetails(List.of(detailRequest));

        Clients client = new Clients(1);
        Users user = new Users(2, "User", "user@example.com", "secret");
        Employees employee = new Employees(4);
        Services serviceEntity = new Services(3, "Corte", 5000);
        serviceEntity.setPrice(5000);

        when(jpaClientRepository.findById(1)).thenReturn(Optional.of(client));
        when(jpaUserRepository.findById(2)).thenReturn(Optional.of(user));
        when(jpaServiceRepository.findById(3)).thenReturn(Optional.of(serviceEntity));
        when(jpaEmployeeRepository.findById(4)).thenReturn(Optional.of(employee));
        when(jpaSalesRepository.save(any(Sales.class))).thenAnswer(invocation -> {
            Sales s = invocation.getArgument(0);
            s.setSaleId(99);
            return s;
        });

        SaleResponseDTO response = service.execute(request);

        assertEquals(99, response.getId());
<<<<<<< HEAD
        assertEquals(15000, response.getTotal());
=======
        assertEquals(10000, response.getTotal());
>>>>>>> 51408e4fe9d7e969b11538bbc8c522b9d34e13aa
        assertEquals("CARD", response.getPaymentType());
        assertEquals(1, response.getClientId());
        assertEquals(2, response.getUserId());
        assertEquals(1, response.getSaleDetails().size());

        assertEquals(3, response.getSaleDetails().get(0).getServiceId());
        assertEquals(4, response.getSaleDetails().get(0).getEmployeeId());
        assertEquals(2, response.getSaleDetails().get(0).getQuantity());
<<<<<<< HEAD
        assertEquals(7500, response.getSaleDetails().get(0).getUnitPrice());
        assertEquals(15000, response.getSaleDetails().get(0).getSubtotal());
=======
        assertEquals(5000, response.getSaleDetails().get(0).getUnitPrice());
        assertEquals(10000, response.getSaleDetails().get(0).getSubtotal());
>>>>>>> 51408e4fe9d7e969b11538bbc8c522b9d34e13aa
    }

    @Test
    void execute_whenSaleDetailsIsEmpty_throwsIllegalArgumentException() {
        JpaSalesRepository jpaSalesRepository = mock(JpaSalesRepository.class);
        JpaClientRepository jpaClientRepository = mock(JpaClientRepository.class);
        JpaUserRepository jpaUserRepository = mock(JpaUserRepository.class);
        JpaAppointmentRepository jpaAppointmentRepository = mock(JpaAppointmentRepository.class);
        JpaEmployeeRepository jpaEmployeeRepository = mock(JpaEmployeeRepository.class);
        JpaServiceRepository jpaServiceRepository = mock(JpaServiceRepository.class);

        CreateSaleService service = new CreateSaleService(
                jpaSalesRepository,
                jpaClientRepository,
                jpaUserRepository,
                jpaAppointmentRepository,
                jpaEmployeeRepository,
                jpaServiceRepository
        );

        SaleRequestDTO request = new SaleRequestDTO();
        request.setClientId(1);
        request.setUserId(2);
        request.setPaymentType("CARD");
        request.setSaleDetails(List.of());

        assertThrows(IllegalArgumentException.class, () -> service.execute(request));
    }
}
