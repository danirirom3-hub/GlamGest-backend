package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.UserResponseDTO;
import com.glamgest.app.application.usecase.GetUserByIdUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserRepository userRepository;

    public GetUserByIdService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO execute(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoleId(),
                user.getActive());
    }
}