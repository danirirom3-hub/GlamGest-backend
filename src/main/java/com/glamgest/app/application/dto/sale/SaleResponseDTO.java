package com.glamgest.app.application.dto.sale;

import java.util.Date;
import java.util.List;

public class SaleResponseDTO {

    private Integer id;
    private Date saleDatetime;
    private Integer total;
    private String paymentType;
    private Integer clientId;
    private Integer userId;
    private List<SaleDetailResponseDTO> saleDetails;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getSaleDatetime() {
        return saleDatetime;
    }

    public void setSaleDatetime(Date saleDatetime) {
        this.saleDatetime = saleDatetime;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

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

    public List<SaleDetailResponseDTO> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<SaleDetailResponseDTO> saleDetails) {
        this.saleDetails = saleDetails;
    }
}
