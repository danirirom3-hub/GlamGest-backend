package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.Sale;
import com.glamgest.app.domain.repository.SaleRepository;
import com.glamgest.app.infrastructure.persistence.entity.Clients;
import com.glamgest.app.infrastructure.persistence.entity.Sales;
import com.glamgest.app.infrastructure.persistence.entity.Users;
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

        return entity;
    }

    private Sale toModel(Sales entity) {
        return new Sale(
                entity.getSaleId(),
                entity.getSaleDatetime(),
                entity.getTotal(),
                entity.getPaymentType(),
                entity.getClientId() != null ? entity.getClientId().getClientId() : null,
                entity.getUserId() != null ? entity.getUserId().getUserId() : null
        );
    }
}
