package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.Sale;
import com.glamgest.app.domain.repository.SaleRepository;
import com.glamgest.app.infrastructure.persistence.entity.Clients;
import com.glamgest.app.infrastructure.persistence.entity.Sales;
import com.glamgest.app.infrastructure.persistence.entity.Users;
import com.glamgest.app.infrastructure.persistence.entity.SaleDetails;
import com.glamgest.app.infrastructure.persistence.entity.Appointments;
import com.glamgest.app.infrastructure.persistence.entity.Employees;
import com.glamgest.app.infrastructure.persistence.entity.Services;
import com.glamgest.app.infrastructure.persistence.repository.JpaSalesRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SaleRepositoryAdapter implements SaleRepository {

    private final JpaSalesRepository jpaSalesRepository;

    public SaleRepositoryAdapter(JpaSalesRepository jpaSalesRepository) {
        this.jpaSalesRepository = jpaSalesRepository;
    }

    @Override
    public Optional<Sale> findById(Integer id) {
        Optional<Sales> entityOpt = jpaSalesRepository.findById(id);
        return entityOpt.map(this::toModel);
    }

    @Override
    public Sale save(Sale sale) {
        Sales entity = toEntity(sale);
        Sales saved = jpaSalesRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public void deleteById(Integer id) {
        jpaSalesRepository.deleteById(id);
    }

    @Override
    public List<Sale> findAll() {
        return jpaSalesRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private Sales toEntity(Sale sale) {
        Sales entity = new Sales();
        if (sale.getId() != null) {
            entity.setSaleId(sale.getId());
        }
        entity.setSaleDatetime(sale.getSaleDatetime());
        entity.setTotal(sale.getTotal());
        entity.setPaymentType(sale.getPaymentType());

        if (sale.getClientId() != null) {
            entity.setClientId(new Clients(sale.getClientId()));
        }
        if (sale.getUserId() != null) {
            entity.setUserId(new Users(sale.getUserId()));
        }
        if (sale.getSaleDetails() != null) {
            entity.setSaleDetailsList(sale.getSaleDetails().stream().map(detail -> {
                SaleDetails detailEntity = new SaleDetails();
                detailEntity.setDetailId(detail.getId());
                detailEntity.setQuantity(detail.getQuantity());
                detailEntity.setUnitPrice(detail.getUnitPrice() == null ? 0 : detail.getUnitPrice());
                detailEntity.setSubtotal(detail.getSubtotal());
                detailEntity.setSaleId(entity);
                if (detail.getAppointmentId() != null) detailEntity.setAppointmentId(new Appointments(detail.getAppointmentId()));
                if (detail.getEmployeeId() != null) detailEntity.setEmployeeId(new Employees(detail.getEmployeeId()));
                if (detail.getServiceId() != null) detailEntity.setServiceId(new Services(detail.getServiceId()));
                return detailEntity;
            }).collect(Collectors.toList()));
        }

        return entity;
    }

    private Sale toModel(Sales entity) {
        List<com.glamgest.app.domain.model.SaleDetail> details = entity.getSaleDetailsList() == null ? null
                : entity.getSaleDetailsList().stream().map(detail -> new com.glamgest.app.domain.model.SaleDetail(
                        detail.getDetailId(),
                        entity.getSaleId(),
                        detail.getAppointmentId() != null ? detail.getAppointmentId().getAppointmentId() : null,
                        detail.getEmployeeId() != null ? detail.getEmployeeId().getEmployeeId() : null,
                        detail.getServiceId() != null ? detail.getServiceId().getServiceId() : null,
                        detail.getQuantity(), detail.getUnitPrice(), detail.getSubtotal())).collect(Collectors.toList());
        return new Sale(
                entity.getSaleId(),
                entity.getSaleDatetime(),
                entity.getTotal(),
                entity.getPaymentType(),
                entity.getClientId() != null ? entity.getClientId().getClientId() : null,
                entity.getUserId() != null ? entity.getUserId().getUserId() : null,
                details
        );
    }
}
