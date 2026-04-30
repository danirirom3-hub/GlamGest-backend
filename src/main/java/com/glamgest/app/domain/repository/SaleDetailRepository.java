package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.SaleDetail;

import java.util.List;
import java.util.Optional;

public interface SaleDetailRepository {

    Optional<SaleDetail> findById(Integer id);

    SaleDetail save(SaleDetail saleDetail);

    void deleteById(Integer id);

    List<SaleDetail> findAll();
}
