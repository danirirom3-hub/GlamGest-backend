package com.glamgest.app.domain.model;

import java.util.Date;

public class Appointment {

    private Integer id;
    private Date appointmentDatetime;
    private String status;
    private String notes;
    private Integer clientId;
    private Integer employeeId;
    private Integer serviceId;
    private Integer userId;

    public Appointment() {
    }

    public Appointment(Integer id, Date appointmentDatetime, String status, String notes, Integer clientId,
            Integer employeeId, Integer serviceId, Integer userId) {
        this.id = id;
        this.appointmentDatetime = appointmentDatetime;
        this.status = status;
        this.notes = notes;
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.serviceId = serviceId;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getAppointmentDatetime() {
        return appointmentDatetime;
    }

    public void setAppointmentDatetime(Date appointmentDatetime) {
        this.appointmentDatetime = appointmentDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
