package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.SaleDetailRequestDTO;
import com.glamgest.app.application.dto.sale.SaleDetailResponseDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.dto.sale.SaleUpdateDTO;
import com.glamgest.app.application.usecase.sale.UpdateSaleUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import com.glamgest.app.domain.model.Sale;
import com.glamgest.app.domain.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateSaleService implements UpdateSaleUseCase {

    private final SaleRepository saleRepository;

    public UpdateSaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    @Transactional
    public SaleResponseDTO execute(SaleUpdateDTO saleUpdateDTO) {
        Sale existingSale = saleRepository.findById(saleUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id " + saleUpdateDTO.getId()));
        if ("VOIDED".equals(existingSale.getStatus())) {
            throw new OperationNotAllowedException("Una venta anulada no se puede modificar");
        }

        // Actualiza el medio de pago cuando se incluye en la petición.
        if (saleUpdateDTO.getPaymentType() != null) {
            existingSale.setPaymentType(saleUpdateDTO.getPaymentType());
        }

        // Los detalles de venta se conservan para mantener el historial de la operación.

        Sale updatedSale = saleRepository.save(existingSale);

        SaleResponseDTO response = new SaleResponseDTO(
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
        response.setStatus(updatedSale.getStatus());
        response.setVoidedAt(updatedSale.getVoidedAt());
        response.setVoidReason(updatedSale.getVoidReason());
        return response;
    }
}
