package com.glamgest.app.application.dto.sale;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SaleUpdateDTO {

    @NotNull(message = "El id de la venta es obligatorio")
    private Integer id;

    private String paymentType;

    private List<SaleDetailRequestDTO> saleDetails;

    public SaleUpdateDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<SaleDetailRequestDTO> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<SaleDetailRequestDTO> saleDetails) {
        this.saleDetails = saleDetails;
    }
}