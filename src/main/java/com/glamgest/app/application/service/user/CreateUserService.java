package com.glamgest.app.application.service.user;

import com.glamgest.app.application.dto.user.UserRequestDTO;
import com.glamgest.app.application.dto.user.UserResponseDTO;
import com.glamgest.app.application.usecase.user.CreateUserUseCase;
import com.glamgest.app.common.exception.DuplicateEmailException;
import com.glamgest.app.common.exception.RoleNotFoundException;
import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.RoleRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
                passwordEncoder.encode(userRequestDTO.getPassword()),
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