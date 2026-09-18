package com.glamgest.app.application.service.auth;

import com.glamgest.app.application.dto.auth.LoginRequestDTO;
import com.glamgest.app.application.dto.auth.LoginResponseDTO;
import com.glamgest.app.application.usecase.auth.LoginUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.UserRepository;

@Service
public class LoginService implements LoginUseCase {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public LoginService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this(authenticationManager, jwtService, null, null);
    }

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, JwtService jwtService,
                        UserRepository userRepository, ClientRepository clientRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public LoginResponseDTO execute(LoginRequestDTO loginRequestDTO) {
        String email = loginRequestDTO.email().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        loginRequestDTO.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().stream().findFirst()
                .map(a -> a.getAuthority()).orElse(null);
        Integer userId = null;
        Integer clientId = null;
        if (userRepository != null) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                userId = user.getId();
                if (clientRepository != null) {
                    clientId = clientRepository.findByUserId(user.getId()).map(c -> c.getId()).orElse(null);
                }
            }
        }

        return new LoginResponseDTO(token, "Bearer", role, userId, clientId);
    }
}
