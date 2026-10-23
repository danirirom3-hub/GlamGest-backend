package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.dto.sale.SaleUpdateDTO;
import com.glamgest.app.application.usecase.sale.UpdateSaleUseCase;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import org.springframework.stereotype.Service;

@Service
public class UpdateSaleService implements UpdateSaleUseCase {

    @Override
    public SaleResponseDTO execute(SaleUpdateDTO saleUpdateDTO) {
        throw new OperationNotAllowedException(
                "Las ventas registradas no se pueden editar. Anúlalas y registra una nueva.");
    }
}
