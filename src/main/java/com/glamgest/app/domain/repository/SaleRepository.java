package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Sale;

import java.util.List;
import java.util.Optional;

public interface SaleRepository {

    Optional<Sale> findById(Integer id);

    Sale save(Sale sale);

    void deleteById(Integer id);

    List<Sale> findAll();
}
