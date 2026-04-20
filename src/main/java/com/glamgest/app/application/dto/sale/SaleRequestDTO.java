package com.glamgest.app.application.dto.sale;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SaleRequestDTO {

    @NotNull(message = "El id del cliente es obligatorio")
    private Integer clientId;

    @NotNull(message = "El id del usuario es obligatorio")
    private Integer userId;

    private String paymentType;

    @NotNull(message = "La lista de detalles de venta es obligatoria")
    private List<SaleDetailRequestDTO> saleDetails;

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
