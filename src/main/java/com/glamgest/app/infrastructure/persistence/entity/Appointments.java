/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.glamgest.app.infrastructure.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;

/**
 *
 * @author angie
 */
@Entity
@Table(name = "appointments", catalog = "glamgest_db", schema = "")
public class Appointments implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "appointment_id")
    private Integer appointmentId;
    @Basic(optional = false)
    @Column(name = "appointment_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmentDatetime;
    @Column(name = "status")
    private String status;
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    @ManyToOne(optional = false)
    private Clients clientId;
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    @ManyToOne(optional = false)
    private Employees employeeId;
    @JoinColumn(name = "service_id", referencedColumnName = "service_id")
    @ManyToOne(optional = false)
    private Services serviceId;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;
    @OneToMany(mappedBy = "appointmentId")
    private List<SaleDetails> saleDetailsList;

    public Appointments() {
    }

    public Appointments(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Appointments(Integer appointmentId, Date appointmentDatetime) {
        this.appointmentId = appointmentId;
        this.appointmentDatetime = appointmentDatetime;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
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

    public Clients getClientId() {
        return clientId;
    }

    public void setClientId(Clients clientId) {
        this.clientId = clientId;
    }

    public Employees getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Employees employeeId) {
        this.employeeId = employeeId;
    }

    public Services getServiceId() {
        return serviceId;
    }

    public void setServiceId(Services serviceId) {
        this.serviceId = serviceId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public List<SaleDetails> getSaleDetailsList() {
        return saleDetailsList;
    }

    public void setSaleDetailsList(List<SaleDetails> saleDetailsList) {
        this.saleDetailsList = saleDetailsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (appointmentId != null ? appointmentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Appointments)) {
            return false;
        }
        Appointments other = (Appointments) object;
        if ((this.appointmentId == null && other.appointmentId != null)
                || (this.appointmentId != null && !this.appointmentId.equals(other.appointmentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Appointments[ appointmentId=" + appointmentId + " ]";
    }

}
