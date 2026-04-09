package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.UserRequestDTO;
import com.glamgest.app.application.dto.UserResponseDTO;
import com.glamgest.app.application.usecase.CreateUserUseCase;
import com.glamgest.app.common.exception.DuplicateEmailException;
import com.glamgest.app.common.exception.RoleNotFoundException;
import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.RoleRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CreateUserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + userRequestDTO.getEmail());
        }

        Integer roleId = userRequestDTO.getRoleId();
        if (roleId == null || !roleRepository.existsById(roleId)) {
            throw new RoleNotFoundException("Role not found with id " + roleId);
        }

        User user = new User(
                null,
                userRequestDTO.getName(),
                userRequestDTO.getEmail(),
                userRequestDTO.getPassword(),
                roleId,
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