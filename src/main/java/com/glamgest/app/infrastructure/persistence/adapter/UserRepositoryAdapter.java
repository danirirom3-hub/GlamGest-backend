package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.UserRepository;
import com.glamgest.app.infrastructure.persistence.entity.UserEntity;
import com.glamgest.app.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaRepository;

    public UserRepositoryAdapter(JpaUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findById(Integer id) {

        Optional<UserEntity> entityOpt = jpaRepository.findById(id);

        return entityOpt.map(entity -> new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRoleId(),
                entity.getActive()));
    }
}