package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.SaleDetailRequestDTO;
import com.glamgest.app.application.dto.sale.SaleDetailResponseDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.dto.sale.SaleUpdateDTO;
import com.glamgest.app.application.usecase.sale.UpdateSaleUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Sale;
import com.glamgest.app.domain.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateSaleService implements UpdateSaleUseCase {

    private final SaleRepository saleRepository;

    public UpdateSaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    public SaleResponseDTO execute(SaleUpdateDTO saleUpdateDTO) {
        Sale existingSale = saleRepository.findById(saleUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id " + saleUpdateDTO.getId()));

        // Update payment type if provided
        if (saleUpdateDTO.getPaymentType() != null) {
            existingSale.setPaymentType(saleUpdateDTO.getPaymentType());
        }

        // For simplicity, if saleDetails are provided, we could update them, but for now, just update payment type
        // In a real scenario, you might need to handle updating sale details more carefully

        Sale updatedSale = saleRepository.save(existingSale);

        return new SaleResponseDTO(
                updatedSale.getId(),
                updatedSale.getSaleDatetime(),
                updatedSale.getTotal(),
                updatedSale.getPaymentType(),
                updatedSale.getClientId(),
                updatedSale.getUserId(),
                updatedSale.getSaleDetails() != null ? updatedSale.getSaleDetails().stream()
                        .map(detail -> new SaleDetailResponseDTO(
                                detail.getAppointmentId(),
                                detail.getEmployeeId(),
                                detail.getServiceId(),
                                detail.getQuantity(),
                                detail.getUnitPrice(),
                                detail.getSubtotal()))
                        .collect(Collectors.toList()) : null);
    }
}