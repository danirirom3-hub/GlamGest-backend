package com.glamgest.app.application.usecase.sale;

import com.glamgest.app.application.dto.sale.SaleResponseDTO;

import java.util.List;

public interface GetAllSalesUseCase {

    List<SaleResponseDTO> execute();
}
