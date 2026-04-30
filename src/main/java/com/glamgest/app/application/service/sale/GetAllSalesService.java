package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.SaleDetailResponseDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.usecase.sale.GetAllSalesUseCase;
import com.glamgest.app.domain.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllSalesService implements GetAllSalesUseCase {

    private final SaleRepository saleRepository;

    public GetAllSalesService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    public List<SaleResponseDTO> execute() {
        return saleRepository.findAll().stream()
                .map(sale -> new SaleResponseDTO(
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
                                .collect(Collectors.toList()) : null))
                .collect(Collectors.toList());
    }
}
