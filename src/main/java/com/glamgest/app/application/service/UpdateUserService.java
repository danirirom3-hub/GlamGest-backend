package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.UserResponseDTO;
import com.glamgest.app.application.dto.UserUpdateDTO;
import com.glamgest.app.application.usecase.UpdateUserUseCase;
import com.glamgest.app.common.exception.DuplicateEmailException;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.exception.RoleNotFoundException;
import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.RoleRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO execute(UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(userUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userUpdateDTO.getId()));

        Integer roleId = userUpdateDTO.getRoleId();
        if (roleId == null || !roleRepository.existsById(roleId)) {
            throw new RoleNotFoundException("Role not found with id " + roleId);
        }

        userRepository.findByEmail(userUpdateDTO.getEmail())
                .filter(user -> !user.getId().equals(existingUser.getId()))
                .ifPresent(user -> {
                    throw new DuplicateEmailException("Email already exists: " + userUpdateDTO.getEmail());
                });

        User user = new User(
                userUpdateDTO.getId(),
                userUpdateDTO.getName(),
                userUpdateDTO.getEmail(),
                passwordEncoder.encode(userUpdateDTO.getPassword()),
                roleId,
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