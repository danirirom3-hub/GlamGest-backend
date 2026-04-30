package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.usecase.sale.DeleteSaleDetailUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.repository.SaleDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteSaleDetailService implements DeleteSaleDetailUseCase {

    private final SaleDetailRepository saleDetailRepository;

    public DeleteSaleDetailService(SaleDetailRepository saleDetailRepository) {
        this.saleDetailRepository = saleDetailRepository;
    }

    @Override
    public void execute(Integer id) {
        if (!saleDetailRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Sale detail not found with id " + id);
        }
        saleDetailRepository.deleteById(id);
    }
}
