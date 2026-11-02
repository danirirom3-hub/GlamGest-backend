package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.SaleDetailResponseDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.usecase.sale.GetAllSalesUseCase;
import com.glamgest.app.domain.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class GetAllSalesService implements GetAllSalesUseCase {

    private final SaleRepository saleRepository;
    private final ZoneId businessZone;

    public GetAllSalesService(SaleRepository saleRepository,
                               @org.springframework.beans.factory.annotation.Value("${business.time-zone:America/Bogota}") String businessTimeZone) {
        this.saleRepository = saleRepository;
        this.businessZone = ZoneId.of(businessTimeZone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> execute() {
        LocalDate today = LocalDate.now(businessZone);
        Date from = Date.from(today.atStartOfDay(businessZone).toInstant());
        Date to = Date.from(today.plusDays(1).atStartOfDay(businessZone).toInstant());
        return saleRepository.findAllBetween(from, to).stream()
                .map(sale -> {
                    SaleResponseDTO response = new SaleResponseDTO(
                        sale.getId(),
                        sale.getSaleDatetime(),
                        sale.getTotal(),
                        sale.getPaymentType(),
                        sale.getClientId(),
                        sale.getUserId(),
                        sale.getSaleDetails() != null ? sale.getSaleDetails().stream()
                                .map(detail -> new SaleDetailResponseDTO(
                                        detail.getAppointmentId(),
                                        detail.getEmployeeId(),
                                        detail.getServiceId(),
                                        detail.getQuantity(),
                                        detail.getUnitPrice(),
                                        detail.getSubtotal()))
                                .collect(Collectors.toList()) : null);
                    response.setStatus(sale.getStatus());
                    response.setVoidedAt(sale.getVoidedAt());
                    response.setVoidReason(sale.getVoidReason());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
