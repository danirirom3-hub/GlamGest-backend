package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.SaleDetail;
import com.glamgest.app.domain.repository.SaleDetailRepository;
import com.glamgest.app.infrastructure.persistence.entity.Appointments;
import com.glamgest.app.infrastructure.persistence.entity.Employees;
import com.glamgest.app.infrastructure.persistence.entity.Sales;
import com.glamgest.app.infrastructure.persistence.entity.SaleDetails;
import com.glamgest.app.infrastructure.persistence.entity.Services;
import com.glamgest.app.infrastructure.persistence.repository.JpaSaleDetailsRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SaleDetailRepositoryAdapter implements SaleDetailRepository {

    private final JpaSaleDetailsRepository jpaSaleDetailsRepository;

    public SaleDetailRepositoryAdapter(JpaSaleDetailsRepository jpaSaleDetailsRepository) {
        this.jpaSaleDetailsRepository = jpaSaleDetailsRepository;
    }

    @Override
    public Optional<SaleDetail> findById(Integer id) {
        Optional<SaleDetails> entityOpt = jpaSaleDetailsRepository.findById(id);
        return entityOpt.map(this::toModel);
    }

    @Override
    public SaleDetail save(SaleDetail saleDetail) {
        SaleDetails entity = toEntity(saleDetail);
        SaleDetails saved = jpaSaleDetailsRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public void deleteById(Integer id) {
        jpaSaleDetailsRepository.deleteById(id);
    }

    @Override
    public List<SaleDetail> findAll() {
        return jpaSaleDetailsRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private SaleDetails toEntity(SaleDetail saleDetail) {
        SaleDetails entity = new SaleDetails();
        if (saleDetail.getId() != null) {
            entity.setDetailId(saleDetail.getId());
        }
        entity.setQuantity(saleDetail.getQuantity());
        entity.setUnitPrice(saleDetail.getUnitPrice());
        entity.setSubtotal(saleDetail.getSubtotal());

        if (saleDetail.getSaleId() != null) {
            entity.setSaleId(new Sales(saleDetail.getSaleId()));
        }
        if (saleDetail.getAppointmentId() != null) {
            entity.setAppointmentId(new Appointments(saleDetail.getAppointmentId()));
        }
        if (saleDetail.getEmployeeId() != null) {
            entity.setEmployeeId(new Employees(saleDetail.getEmployeeId()));
        }
        if (saleDetail.getServiceId() != null) {
            entity.setServiceId(new Services(saleDetail.getServiceId()));
        }

        return entity;
    }

    private SaleDetail toModel(SaleDetails entity) {
        return new SaleDetail(
                entity.getDetailId(),
                entity.getSaleId() != null ? entity.getSaleId().getSaleId() : null,
                entity.getAppointmentId() != null ? entity.getAppointmentId().getAppointmentId() : null,
                entity.getEmployeeId() != null ? entity.getEmployeeId().getEmployeeId() : null,
                entity.getServiceId() != null ? entity.getServiceId().getServiceId() : null,
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSubtotal()
        );
    }
}
