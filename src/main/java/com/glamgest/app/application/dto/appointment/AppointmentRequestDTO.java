package com.glamgest.app.application.dto.appointment;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class AppointmentRequestDTO {

    @NotNull(message = "La fecha de la cita es obligatoria")
    private Date appointmentDatetime;

    private String notes;

    @NotNull(message = "El id del cliente es obligatorio")
    private Integer clientId;

    @NotNull(message = "El id del empleado es obligatorio")
    private Integer employeeId;

    @NotNull(message = "El id del servicio es obligatorio")
    private Integer serviceId;

    @NotNull(message = "El id del usuario es obligatorio")
    private Integer userId;

    public AppointmentRequestDTO() {
    }

    public Date getAppointmentDatetime() {
        return appointmentDatetime;
    }

    public void setAppointmentDatetime(Date appointmentDatetime) {
        this.appointmentDatetime = appointmentDatetime;
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
