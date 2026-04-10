package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    Client save(Client client);

    Optional<Client> findById(Integer id);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    void deleteById(Integer id);

    List<Client> findAll();
}