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
    private String status;
    private Date voidedAt;
    private String voidReason;
    private List<SaleDetailResponseDTO> saleDetails;

    public SaleResponseDTO() {
    }

    public SaleResponseDTO(Integer id, Date saleDatetime, Integer total, String paymentType,
                          Integer clientId, Integer userId, List<SaleDetailResponseDTO> saleDetails) {
        this.id = id;
        this.saleDatetime = saleDatetime;
        this.total = total;
        this.paymentType = paymentType;
        this.clientId = clientId;
        this.userId = userId;
        this.saleDetails = saleDetails;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getVoidedAt() {
        return voidedAt;
    }

    public void setVoidedAt(Date voidedAt) {
        this.voidedAt = voidedAt;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }

    public List<SaleDetailResponseDTO> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<SaleDetailResponseDTO> saleDetails) {
        this.saleDetails = saleDetails;
    }
}
