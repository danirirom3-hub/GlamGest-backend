package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.usecase.sale.DeleteSaleUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.repository.SaleRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteSaleService implements DeleteSaleUseCase {

    private final SaleRepository saleRepository;

    public DeleteSaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    public void execute(Integer id) {
        if (!saleRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Sale not found with id " + id);
        }
        saleRepository.deleteById(id);
    }
}
