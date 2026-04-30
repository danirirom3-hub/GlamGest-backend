package com.glamgest.app.domain.model;

public class SaleDetail {

    private Integer id;
    private Integer saleId;
    private Integer appointmentId;
    private Integer employeeId;
    private Integer serviceId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer subtotal;

    public SaleDetail() {
    }

    public SaleDetail(Integer id, Integer saleId, Integer appointmentId, Integer employeeId, Integer serviceId,
            Integer quantity, Integer unitPrice, Integer subtotal) {
        this.id = id;
        this.saleId = saleId;
        this.appointmentId = appointmentId;
        this.employeeId = employeeId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }
}
