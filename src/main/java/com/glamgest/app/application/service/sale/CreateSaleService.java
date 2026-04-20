package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.SaleDetailRequestDTO;
import com.glamgest.app.application.dto.sale.SaleDetailResponseDTO;
import com.glamgest.app.application.dto.sale.SaleRequestDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.usecase.sale.CreateSaleUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.infrastructure.persistence.entity.Appointments;
import com.glamgest.app.infrastructure.persistence.entity.Employees;
import com.glamgest.app.infrastructure.persistence.entity.SaleDetails;
import com.glamgest.app.infrastructure.persistence.entity.Sales;
import com.glamgest.app.infrastructure.persistence.entity.Services;
import com.glamgest.app.infrastructure.persistence.repository.JpaAppointmentRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaClientRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaEmployeeRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaSalesRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaServiceRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreateSaleService implements CreateSaleUseCase {

    private final JpaSalesRepository jpaSalesRepository;
    private final JpaClientRepository jpaClientRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final JpaEmployeeRepository jpaEmployeeRepository;
    private final JpaServiceRepository jpaServiceRepository;

    public CreateSaleService(JpaSalesRepository jpaSalesRepository,
                             JpaClientRepository jpaClientRepository,
                             JpaUserRepository jpaUserRepository,
                             JpaAppointmentRepository jpaAppointmentRepository,
                             JpaEmployeeRepository jpaEmployeeRepository,
                             JpaServiceRepository jpaServiceRepository) {
        this.jpaSalesRepository = jpaSalesRepository;
        this.jpaClientRepository = jpaClientRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.jpaEmployeeRepository = jpaEmployeeRepository;
        this.jpaServiceRepository = jpaServiceRepository;
    }

    @Override
    public SaleResponseDTO execute(SaleRequestDTO saleRequestDTO) {
        if (saleRequestDTO.getSaleDetails() == null || saleRequestDTO.getSaleDetails().isEmpty()) {
            throw new IllegalArgumentException("Sale must contain at least one sale detail");
        }

        Sales sale = new Sales();
        sale.setSaleDatetime(new Date());
        sale.setClientId(jpaClientRepository.findById(saleRequestDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + saleRequestDTO.getClientId())));
        sale.setUserId(jpaUserRepository.findById(saleRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + saleRequestDTO.getUserId())));
        sale.setPaymentType(saleRequestDTO.getPaymentType());

        List<SaleDetails> saleDetails = saleRequestDTO.getSaleDetails().stream()
                .map(detailRequest -> buildSaleDetail(detailRequest, sale))
                .collect(Collectors.toList());

        int total = saleDetails.stream()
                .mapToInt(detail -> detail.getSubtotal() == null ? 0 : detail.getSubtotal())
                .sum();

        sale.setTotal(total);
        sale.setSaleDetailsList(saleDetails);
        saleDetails.forEach(detail -> detail.setSaleId(sale));

        Sales savedSale = jpaSalesRepository.save(sale);
        return toSaleResponseDTO(savedSale);
    }

    private SaleDetails buildSaleDetail(SaleDetailRequestDTO detailRequest, Sales sale) {
        Appointments appointment = null;
        if (detailRequest.getAppointmentId() != null) {
            appointment = jpaAppointmentRepository.findById(detailRequest.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + detailRequest.getAppointmentId()));
        }

        Services service = null;
        if (appointment != null) {
            service = appointment.getServiceId();
            if (service == null) {
                throw new IllegalArgumentException("Appointment does not contain a linked service");
            }
        } else if (detailRequest.getServiceId() != null) {
            service = jpaServiceRepository.findById(detailRequest.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found with id " + detailRequest.getServiceId()));
        } else {
            throw new IllegalArgumentException("Sale detail must include a service when appointment is not provided");
        }

        Employees employee;
        if (detailRequest.getEmployeeId() != null) {
            employee = jpaEmployeeRepository.findById(detailRequest.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + detailRequest.getEmployeeId()));
        } else if (appointment != null) {
            employee = appointment.getEmployeeId();
            if (employee == null) {
                throw new IllegalArgumentException("Appointment does not contain a linked employee");
            }
        } else {
            throw new IllegalArgumentException("Sale detail must include an employee when appointment is not provided");
        }

        int quantity = detailRequest.getQuantity() != null && detailRequest.getQuantity() > 0 ? detailRequest.getQuantity() : 1;
        int unitPrice = detailRequest.getUnitPrice() != null && detailRequest.getUnitPrice() > 0
                ? detailRequest.getUnitPrice()
                : service.getPrice();

        SaleDetails detail = new SaleDetails();
        detail.setAppointmentId(appointment);
        detail.setEmployeeId(employee);
        detail.setServiceId(service);
        detail.setQuantity(quantity);
        detail.setUnitPrice(unitPrice);
        detail.setSubtotal(quantity * unitPrice);
        detail.setSaleId(sale);
        return detail;
    }

    private SaleResponseDTO toSaleResponseDTO(Sales sale) {
        SaleResponseDTO response = new SaleResponseDTO();
        response.setId(sale.getSaleId());
        response.setSaleDatetime(sale.getSaleDatetime());
        response.setTotal(sale.getTotal());
        response.setPaymentType(sale.getPaymentType());
        response.setClientId(sale.getClientId() != null ? sale.getClientId().getClientId() : null);
        response.setUserId(sale.getUserId() != null ? sale.getUserId().getUserId() : null);
        response.setSaleDetails(sale.getSaleDetailsList().stream()
                .map(this::toSaleDetailResponseDTO)
                .collect(Collectors.toList()));
        return response;
    }

    private SaleDetailResponseDTO toSaleDetailResponseDTO(SaleDetails detail) {
        SaleDetailResponseDTO response = new SaleDetailResponseDTO();
        response.setAppointmentId(detail.getAppointmentId() != null ? detail.getAppointmentId().getAppointmentId() : null);
        response.setEmployeeId(detail.getEmployeeId() != null ? detail.getEmployeeId().getEmployeeId() : null);
        response.setServiceId(detail.getServiceId() != null ? detail.getServiceId().getServiceId() : null);
        response.setQuantity(detail.getQuantity());
        response.setUnitPrice(detail.getUnitPrice());
        response.setSubtotal(detail.getSubtotal());
        return response;
    }
}
