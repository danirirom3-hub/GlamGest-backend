package com.glamgest.app.application.service.user;

import com.glamgest.app.application.dto.user.UserRequestDTO;
import com.glamgest.app.application.dto.user.UserResponseDTO;
import com.glamgest.app.common.exception.DuplicateEmailException;
import com.glamgest.app.common.exception.RoleNotFoundException;
import com.glamgest.app.domain.model.Role;
import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.RoleRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateUserServiceTest {

    @Test
    void execute_whenUserDoesNotExist_returnsCreatedUserResponse() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        CreateUserService service = new CreateUserService(userRepository, roleRepository, passwordEncoder);

        UserRequestDTO request = new UserRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRoleId(1);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.existsById(1)).thenReturn(true);
        when(roleRepository.findById(1)).thenReturn(Optional.of(new Role(1, "ROLE_USER", "Standard role")));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        User savedUser = new User(10, "Test User", "test@example.com", "encoded-password", 1, "ROLE_USER", true);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO response = service.execute(request);

        assertEquals(10, response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(1, response.getRoleId());
        assertEquals("ROLE_USER", response.getRoleName());
        assertEquals(true, response.getActive());
    }

    @Test
    void execute_whenEmailAlreadyExists_throwsDuplicateEmailException() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        CreateUserService service = new CreateUserService(userRepository, roleRepository, passwordEncoder);

        UserRequestDTO request = new UserRequestDTO();
        request.setName("Test User");
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setRoleId(1);

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> service.execute(request));
    }

    @Test
    void execute_whenRoleDoesNotExist_throwsRoleNotFoundException() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        CreateUserService service = new CreateUserService(userRepository, roleRepository, passwordEncoder);

        UserRequestDTO request = new UserRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRoleId(99);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.existsById(99)).thenReturn(false);

        assertThrows(RoleNotFoundException.class, () -> service.execute(request));
    }
}
