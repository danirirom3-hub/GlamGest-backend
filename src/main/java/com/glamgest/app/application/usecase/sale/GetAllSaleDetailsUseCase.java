package com.glamgest.app.application.usecase.sale;

import com.glamgest.app.application.dto.sale.SaleDetailResponseDTO;

import java.util.List;

public interface GetAllSaleDetailsUseCase {

    List<SaleDetailResponseDTO> execute();
}
