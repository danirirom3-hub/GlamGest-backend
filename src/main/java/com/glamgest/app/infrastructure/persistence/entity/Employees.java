package com.glamgest.app.infrastructure.persistence.entity;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "employees", catalog = "glamgest_db", schema = "")
@NamedQueries({
        @NamedQuery(name = "Employees.findAll", query = "SELECT e FROM Employees e") })
public class Employees implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "employee_id")
    private Integer employeeId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "phone")
    private String phone;
    @Column(name = "active", nullable = false)
    private Boolean active;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "employeeId")
    private List<Appointments> appointmentsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "employeeId")
    private List<SaleDetails> saleDetailsList;

    public Employees() {
    }

    public Employees(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Employees(Integer employeeId, String name) {
        this.employeeId = employeeId;
        this.name = name;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Employees)) {
            return false;
        }
        Employees other = (Employees) object;
        if ((this.employeeId == null && other.employeeId != null)
                || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Employees[ employeeId=" + employeeId + " ]";
    }

}
