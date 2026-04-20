package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.sale.SaleRequestDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.usecase.sale.CreateSaleUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final CreateSaleUseCase createSaleUseCase;

    public SaleController(CreateSaleUseCase createSaleUseCase) {
        this.createSaleUseCase = createSaleUseCase;
    }

    @PostMapping
    public ResponseEntity<?> createSale(@Valid @RequestBody SaleRequestDTO saleRequestDTO,
                                        BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        SaleResponseDTO response = createSaleUseCase.execute(saleRequestDTO);
        return BuilderHelper.buildResponse(response, "Venta creada", HttpStatus.CREATED, true);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
