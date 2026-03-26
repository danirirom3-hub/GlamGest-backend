package com.glamgest.app.infrastructure.persistence.entity;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 *
 * @author angie
 */
@Entity
@Table(name = "sale_details", catalog = "glamgest_db", schema = "")
@NamedQueries({
    @NamedQuery(name = "SaleDetails.findAll", query = "SELECT s FROM SaleDetails s")})
public class SaleDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "detail_id")
    private Integer detailId;
    @Column(name = "quantity")
    private Integer quantity;
    @Basic(optional = false)
    @Column(name = "unit_price")
    private int unitPrice;
    @Column(name = "subtotal")
    private Integer subtotal;
    @JoinColumn(name = "appointment_id", referencedColumnName = "appointment_id")
    @ManyToOne
    private Appointments appointmentId;
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    @ManyToOne(optional = false)
    private Employees employeeId;
    @JoinColumn(name = "sale_id", referencedColumnName = "sale_id")
    @ManyToOne(optional = false)
    private Sales saleId;
    @JoinColumn(name = "service_id", referencedColumnName = "service_id")
    @ManyToOne(optional = false)
    private Services serviceId;

    public SaleDetails() {
    }

    public SaleDetails(Integer detailId) {
        this.detailId = detailId;
    }

    public SaleDetails(Integer detailId, int unitPrice) {
        this.detailId = detailId;
        this.unitPrice = unitPrice;
    }

    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public Appointments getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Appointments appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Employees getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Employees employeeId) {
        this.employeeId = employeeId;
    }

    public Sales getSaleId() {
        return saleId;
    }

    public void setSaleId(Sales saleId) {
        this.saleId = saleId;
    }

    public Services getServiceId() {
        return serviceId;
    }

    public void setServiceId(Services serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (detailId != null ? detailId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SaleDetails)) {
            return false;
        }
        SaleDetails other = (SaleDetails) object;
        if ((this.detailId == null && other.detailId != null) || (this.detailId != null && !this.detailId.equals(other.detailId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SaleDetails[ detailId=" + detailId + " ]";
    }
    
}
