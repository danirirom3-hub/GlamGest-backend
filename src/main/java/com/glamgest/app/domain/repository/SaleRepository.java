package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Sale;

import java.util.List;
import java.util.Optional;
import java.util.Date;

public interface SaleRepository {

    Optional<Sale> findById(Integer id);

    Sale save(Sale sale);

    void deleteById(Integer id);

    void voidById(Integer id);

    List<Sale> findAll();

    List<Sale> findAllBetween(Date from, Date to);
}
