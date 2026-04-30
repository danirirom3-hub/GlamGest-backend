package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.SaleDetailResponseDTO;
import com.glamgest.app.application.usecase.sale.GetAllSaleDetailsUseCase;
import com.glamgest.app.domain.repository.SaleDetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllSaleDetailsService implements GetAllSaleDetailsUseCase {

    private final SaleDetailRepository saleDetailRepository;

    public GetAllSaleDetailsService(SaleDetailRepository saleDetailRepository) {
        this.saleDetailRepository = saleDetailRepository;
    }

    @Override
    public List<SaleDetailResponseDTO> execute() {
        return saleDetailRepository.findAll().stream()
                .map(detail -> new SaleDetailResponseDTO(
                        detail.getAppointmentId(),
                        detail.getEmployeeId(),
                        detail.getServiceId(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getSubtotal()))
                .collect(Collectors.toList());
    }
}
