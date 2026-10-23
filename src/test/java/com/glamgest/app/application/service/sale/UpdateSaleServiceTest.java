package com.glamgest.app.application.service.sale;

import com.glamgest.app.application.dto.sale.SaleUpdateDTO;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSaleServiceTest {

    @Test
    void registeredSalesMustBeVoidedInsteadOfEdited() {
        UpdateSaleService service = new UpdateSaleService();

        assertThrows(OperationNotAllowedException.class,
                () -> service.execute(new SaleUpdateDTO()));
    }
}
