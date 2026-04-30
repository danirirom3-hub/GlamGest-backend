package com.glamgest.app.domain.model;

import java.util.Date;
import java.util.List;

public class Sale {

    private Integer id;
    private Date saleDatetime;
    private Integer total;
    private String paymentType;
    private Integer clientId;
    private Integer userId;
    private List<SaleDetail> saleDetails;

    public Sale() {
    }

    public Sale(Integer id, Date saleDatetime, Integer total, String paymentType, Integer clientId, Integer userId) {
        this.id = id;
        this.saleDatetime = saleDatetime;
        this.total = total;
        this.paymentType = paymentType;
        this.clientId = clientId;
        this.userId = userId;
    }

    public Sale(Integer id, Date saleDatetime, Integer total, String paymentType, Integer clientId, Integer userId,
            List<SaleDetail> saleDetails) {
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

    public List<SaleDetail> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<SaleDetail> saleDetails) {
        this.saleDetails = saleDetails;
    }
}
