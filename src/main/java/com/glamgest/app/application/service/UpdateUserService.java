package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.UserResponseDTO;
import com.glamgest.app.application.dto.UserUpdateDTO;
import com.glamgest.app.application.usecase.UpdateUserUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepository userRepository;

    public UpdateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO execute(UserUpdateDTO userUpdateDTO) {
        // Check if user exists
        userRepository.findById(userUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userUpdateDTO.getId()));

        User user = new User(
                userUpdateDTO.getId(),
                userUpdateDTO.getName(),
                userUpdateDTO.getEmail(),
                userUpdateDTO.getPassword(),
                userUpdateDTO.getRoleId(),
                userUpdateDTO.getActive()
        );

        User updatedUser = userRepository.save(user);

        return new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRoleId(),
                updatedUser.getActive()
        );
    }
}