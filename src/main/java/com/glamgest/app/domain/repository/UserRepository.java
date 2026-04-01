package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Integer id);

    User save(User user);

    void deleteById(Integer id);

    List<User> findAll();
}