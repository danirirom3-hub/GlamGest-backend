package com.glamgest.app.application.service.auth;

import com.glamgest.app.application.dto.auth.LoginResponseDTO;
import com.glamgest.app.application.dto.auth.RegisterRequestDTO;
import com.glamgest.app.application.usecase.auth.RegisterUseCase;
import com.glamgest.app.common.exception.DuplicateEmailException;
import com.glamgest.app.common.exception.DuplicateClientPhoneException;
import com.glamgest.app.common.exception.RoleNotFoundException;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.model.Role;
import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.RoleRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class RegisterService implements RegisterUseCase {

    private static final String CLIENT_ROLE = "CLIENT";

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public RegisterService(UserRepository userRepository, ClientRepository clientRepository,
                           RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public LoginResponseDTO execute(RegisterRequestDTO request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("El email ya tiene una cuenta");
        }

        Role clientRole = roleRepository.findByName(CLIENT_ROLE)
                .orElseThrow(() -> new RoleNotFoundException("El rol CLIENT no está configurado"));
        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client != null && client.getUserId() != null) {
            throw new DuplicateEmailException("El cliente ya tiene una cuenta");
        }
        if (client == null && clientRepository.existsByPhone(request.phone())) {
            throw new DuplicateClientPhoneException("El teléfono ya pertenece a otro cliente");
        }
        if (client != null && client.getPhone() != null && !request.phone().equals(client.getPhone())) {
            throw new DuplicateClientPhoneException("El teléfono no coincide con el perfil existente");
        }

        User user = userRepository.save(new User(null, request.name().trim(), email,
                passwordEncoder.encode(request.password()), clientRole.getId(), clientRole.getName(), true));

        if (client == null) {
            client = new Client(null, request.name().trim(), email, request.phone(), new Date(), user.getId());
        } else {
            client.setUserId(user.getId());
            if (client.getPhone() == null || client.getPhone().isBlank()) {
                client.setPhone(request.phone());
            }
        }
        clientRepository.save(client);

        var principal = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), java.util.List.of(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(clientRole.getName())));
        return new LoginResponseDTO(jwtService.generateToken(principal), "Bearer", clientRole.getName(), user.getId(), client.getId());
    }
}
