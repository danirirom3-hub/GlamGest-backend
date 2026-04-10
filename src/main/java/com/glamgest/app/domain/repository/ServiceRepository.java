package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Service;
import java.util.List;
import java.util.Optional;

public interface ServiceRepository {

    Service save(Service service);

    Optional<Service> findById(Integer id);

    List<Service> findAll();

    void deleteById(Integer id);

    boolean existsByName(String name);

    Optional<Service> findByName(String name);
}
