package com.glamgest.app.infrastructure.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "sales", catalog = "glamgest_db", schema = "")
@NamedQueries({
        @NamedQuery(name = "Sales.findAll", query = "SELECT s FROM Sales s") })
public class Sales implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "sale_id")
    private Integer saleId;
    @Column(name = "sale_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date saleDatetime;
    @Basic(optional = false)
    @Column(name = "total")
    private int total;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "saleId")
    private List<SaleDetails> saleDetailsList;
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    @ManyToOne(optional = false)
    private Clients clientId;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;

    public Sales() {
    }

    public Sales(Integer saleId) {
        this.saleId = saleId;
    }

    public Sales(Integer saleId, int total) {
        this.saleId = saleId;
        this.total = total;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public Date getSaleDatetime() {
        return saleDatetime;
    }

    public void setSaleDatetime(Date saleDatetime) {
        this.saleDatetime = saleDatetime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SaleDetails> getSaleDetailsList() {
        return saleDetailsList;
    }

    public void setSaleDetailsList(List<SaleDetails> saleDetailsList) {
        this.saleDetailsList = saleDetailsList;
    }

    public Clients getClientId() {
        return clientId;
    }

    public void setClientId(Clients clientId) {
        this.clientId = clientId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (saleId != null ? saleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Sales)) {
            return false;
        }
        Sales other = (Sales) object;
        if ((this.saleId == null && other.saleId != null)
                || (this.saleId != null && !this.saleId.equals(other.saleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Sales[ saleId=" + saleId + " ]";
    }

}
