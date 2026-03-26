package com.glamgest.app.infrastructure.persistence.entity;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "services", catalog = "glamgest_db", schema = "")
public class Services implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "service_id")
    private Integer serviceId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Basic(optional = false)
    @Column(name = "price")
    private int price;
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    @Column(name = "active", nullable = false)
    private Boolean active;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceId")
    private List<Appointments> appointmentsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceId")
    private List<SaleDetails> saleDetailsList;

    public Services() {
    }

    public Services(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Services(Integer serviceId, String name, int price) {
        this.serviceId = serviceId;
        this.name = name;
        this.price = price;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Appointments> getAppointmentsList() {
        return appointmentsList;
    }

    public void setAppointmentsList(List<Appointments> appointmentsList) {
        this.appointmentsList = appointmentsList;
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
        hash += (serviceId != null ? serviceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Services)) {
            return false;
        }
        Services other = (Services) object;
        if ((this.serviceId == null && other.serviceId != null)
                || (this.serviceId != null && !this.serviceId.equals(other.serviceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Services[ serviceId=" + serviceId + " ]";
    }

}
