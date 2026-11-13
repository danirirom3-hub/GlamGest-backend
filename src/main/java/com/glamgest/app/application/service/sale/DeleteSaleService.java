package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.usecase.sale.DeleteSaleUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import com.glamgest.app.domain.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteSaleService implements DeleteSaleUseCase {

    private final SaleRepository saleRepository;

    public DeleteSaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    @Transactional
    public void execute(Integer id) {
        var sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id " + id));
        if ("VOIDED".equals(sale.getStatus())) {
            throw new OperationNotAllowedException("La venta ya está anulada");
        }
        if (saleRepository.voidById(id) == 0) {
            throw new OperationNotAllowedException("La venta no pudo ser anulada");
        }
    }
}
