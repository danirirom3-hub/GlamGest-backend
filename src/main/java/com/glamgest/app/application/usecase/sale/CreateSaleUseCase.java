package com.glamgest.app.application.usecase.sale;

import com.glamgest.app.application.dto.sale.SaleRequestDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;

public interface CreateSaleUseCase {

    SaleResponseDTO execute(SaleRequestDTO saleRequestDTO);
}
