package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.sale.SaleRequestDTO;
import com.glamgest.app.application.dto.sale.SaleResponseDTO;
import com.glamgest.app.application.dto.sale.SaleUpdateDTO;
import com.glamgest.app.application.service.sale.CashierGroupService;
import com.glamgest.app.application.usecase.sale.CreateSaleUseCase;
import com.glamgest.app.application.usecase.sale.DeleteSaleUseCase;
import com.glamgest.app.application.usecase.sale.GetAllSalesUseCase;
import com.glamgest.app.application.usecase.sale.UpdateSaleUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import com.glamgest.app.infrastructure.configuration.AuditLogContext;
import com.glamgest.app.application.service.audit.AuditLogResourceNameResolver;
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
    private final AuditLogContext auditLogContext;
    private final AuditLogResourceNameResolver auditLogResourceNameResolver;
    private final CashierGroupService cashierGroupService;

    public SaleController(CreateSaleUseCase createSaleUseCase,
                          GetAllSalesUseCase getAllSalesUseCase,
                          UpdateSaleUseCase updateSaleUseCase,
                          DeleteSaleUseCase deleteSaleUseCase,
                          AuditLogContext auditLogContext,
                          AuditLogResourceNameResolver auditLogResourceNameResolver,
                          CashierGroupService cashierGroupService) {
        this.createSaleUseCase = createSaleUseCase;
        this.getAllSalesUseCase = getAllSalesUseCase;
        this.updateSaleUseCase = updateSaleUseCase;
        this.deleteSaleUseCase = deleteSaleUseCase;
        this.auditLogContext = auditLogContext;
        this.auditLogResourceNameResolver = auditLogResourceNameResolver;
        this.cashierGroupService = cashierGroupService;
    }

    @PostMapping
    public ResponseEntity<?> createSale(@Valid @RequestBody SaleRequestDTO saleRequestDTO,
                                        BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        SaleResponseDTO response = createSaleUseCase.execute(saleRequestDTO);
        auditLogContext.setResource(response.getId().toString(), auditLogResourceNameResolver.sale(response.getId()));
        return BuilderHelper.buildResponse(response, "Venta creada", HttpStatus.CREATED, true);
    }

    @GetMapping
    public ResponseEntity<?> getAllSales() {
        List<SaleResponseDTO> sales = getAllSalesUseCase.execute();
        return BuilderHelper.buildResponse(sales, "Ventas obtenidas", HttpStatus.OK, true);
    }

    @GetMapping("/cashier-group")
    public ResponseEntity<?> getCashierGroup(@RequestParam Integer appointmentId) {
        return BuilderHelper.buildResponse(cashierGroupService.getForAppointment(appointmentId),
                "Citas disponibles para caja", HttpStatus.OK, true);
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
        auditLogContext.setResource(response.getId().toString(), auditLogResourceNameResolver.sale(response.getId()));
        return BuilderHelper.buildResponse(response, "Venta actualizada", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable Integer id) {
        deleteSaleUseCase.execute(id);
        auditLogContext.setResource(id.toString(), auditLogResourceNameResolver.sale(id));
        return BuilderHelper.buildResponse(null, "Venta anulada", HttpStatus.NO_CONTENT, true);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
