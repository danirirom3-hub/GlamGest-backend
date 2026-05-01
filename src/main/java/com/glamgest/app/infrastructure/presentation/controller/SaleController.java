package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.sale.SaleRequestDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.dto.sale.SaleUpdateDTO;
import com.glamgest.app.application.usecase.sale.CreateSaleUseCase;
import com.glamgest.app.application.usecase.sale.DeleteSaleUseCase;
import com.glamgest.app.application.usecase.sale.GetAllSalesUseCase;
import com.glamgest.app.application.usecase.sale.UpdateSaleUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final CreateSaleUseCase createSaleUseCase;
    private final GetAllSalesUseCase getAllSalesUseCase;
    private final UpdateSaleUseCase updateSaleUseCase;
    private final DeleteSaleUseCase deleteSaleUseCase;

    public SaleController(CreateSaleUseCase createSaleUseCase,
                          GetAllSalesUseCase getAllSalesUseCase,
                          UpdateSaleUseCase updateSaleUseCase,
                          DeleteSaleUseCase deleteSaleUseCase) {
        this.createSaleUseCase = createSaleUseCase;
        this.getAllSalesUseCase = getAllSalesUseCase;
        this.updateSaleUseCase = updateSaleUseCase;
        this.deleteSaleUseCase = deleteSaleUseCase;
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

    @GetMapping
    public ResponseEntity<?> getAllSales() {
        List<SaleResponseDTO> sales = getAllSalesUseCase.execute();
        return BuilderHelper.buildResponse(sales, "Ventas obtenidas", HttpStatus.OK, true);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable Integer id,
                                        @Valid @RequestBody SaleUpdateDTO saleUpdateDTO,
                                        BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        saleUpdateDTO.setId(id);
        SaleResponseDTO response = updateSaleUseCase.execute(saleUpdateDTO);
        return BuilderHelper.buildResponse(response, "Venta actualizada", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable Integer id) {
        deleteSaleUseCase.execute(id);
        return BuilderHelper.buildResponse(null, "Venta eliminada", HttpStatus.NO_CONTENT, true);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
