package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.UserRepository;
import com.glamgest.app.infrastructure.persistence.entity.Roles;
import com.glamgest.app.infrastructure.persistence.entity.Users;
import com.glamgest.app.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaRepository;

    public UserRepositoryAdapter(JpaUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findById(Integer id) {
        Optional<Users> entityOpt = jpaRepository.findById(id);
        
        // Filter out soft-deleted users (active = false)
        return entityOpt
                .filter(entity -> entity.getActive() != null && entity.getActive())
                .map(entity -> new User(
                        entity.getUserId(),
                        entity.getName(),
                        entity.getEmail(),
                        entity.getPassword(),
                        entity.getRoleId().getRoleId(),
                        entity.getRoleId().getName(),
                        entity.getActive()));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(entity -> new User(
                        entity.getUserId(),
                        entity.getName(),
                        entity.getEmail(),
                        entity.getPassword(),
                        entity.getRoleId().getRoleId(),
                        entity.getRoleId().getName(),
                        entity.getActive()));
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        Users entity = new Users();
        if (user.getId() != null) {
            entity.setUserId(user.getId());
        }
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setActive(user.getActive());

        Roles role = new Roles();
        role.setRoleId(user.getRoleId());
        entity.setRoleId(role);

        Users savedEntity = jpaRepository.save(entity);

        return new User(
                savedEntity.getUserId(),
                savedEntity.getName(),
                savedEntity.getEmail(),
                savedEntity.getPassword(),
                savedEntity.getRoleId().getRoleId(),
                savedEntity.getRoleId().getName(),
                savedEntity.getActive());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.softDelete(id);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getActive() != null && entity.getActive())
                .map(entity -> new User(
                        entity.getUserId(),
                        entity.getName(),
                        entity.getEmail(),
                        entity.getPassword(),
                        entity.getRoleId().getRoleId(),
                        entity.getRoleId().getName(),
                        entity.getActive()))
                .collect(Collectors.toList());
    }
}