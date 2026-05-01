package com.glamgest.app.application.usecase.sale;

import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.dto.sale.SaleUpdateDTO;

public interface UpdateSaleUseCase {

    SaleResponseDTO execute(SaleUpdateDTO saleUpdateDTO);
}