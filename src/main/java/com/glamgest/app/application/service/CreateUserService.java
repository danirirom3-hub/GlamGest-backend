package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.UserRequestDTO;
import com.glamgest.app.application.dto.UserResponseDTO;
import com.glamgest.app.application.usecase.CreateUserUseCase;
import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;

    public CreateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
        User user = new User(
                null, // ID will be generated
                userRequestDTO.getName(),
                userRequestDTO.getEmail(),
                userRequestDTO.getPassword(),
                userRequestDTO.getRoleId(),
                userRequestDTO.getActive()
        );

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRoleId(),
                savedUser.getActive()
        );
    }
}