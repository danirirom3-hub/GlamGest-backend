package com.glamgest.app.application.service.user;

import com.glamgest.app.application.dto.user.UserResponseDTO;
import com.glamgest.app.application.usecase.user.GetAllUsersUseCase;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllUsersService implements GetAllUsersUseCase {

    private final UserRepository userRepository;

    public GetAllUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponseDTO> execute() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRoleId(),
                        user.getRoleName(),
                        user.getActive()))
                .collect(Collectors.toList());
    }
}