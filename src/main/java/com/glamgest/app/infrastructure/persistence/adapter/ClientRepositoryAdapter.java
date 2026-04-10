package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.infrastructure.persistence.entity.Clients;
import com.glamgest.app.infrastructure.persistence.repository.JpaClientRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ClientRepositoryAdapter implements ClientRepository {

    private final JpaClientRepository jpaClientRepository;

    public ClientRepositoryAdapter(JpaClientRepository jpaClientRepository) {
        this.jpaClientRepository = jpaClientRepository;
    }

    @Override
    public Client save(Client client) {
        Clients entity = toEntity(client);
        Clients saved = jpaClientRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Client> findById(Integer id) {
        return jpaClientRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return jpaClientRepository.findByEmail(email).map(this::toModel);
    }

    @Override
    public Optional<Client> findByPhone(String phone) {
        return jpaClientRepository.findByPhone(phone).map(this::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaClientRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return jpaClientRepository.existsByPhone(phone);
    }

    @Override
    public void deleteById(Integer id) {
        jpaClientRepository.deleteById(id);
    }

    @Override
    public List<Client> findAll() {
        return jpaClientRepository.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    private Clients toEntity(Client client) {
        Clients entity = new Clients();
        if (client.getId() != null) {
            entity.setClientId(client.getId());
        }
        entity.setName(client.getName());
        entity.setEmail(client.getEmail());
        entity.setPhone(client.getPhone());
        entity.setRegistrationDate(client.getRegistrationDate());
        return entity;
    }

    private Client toModel(Clients entity) {
        return new Client(
                entity.getClientId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getRegistrationDate()
        );
    }
}