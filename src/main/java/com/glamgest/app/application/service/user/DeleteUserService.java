package com.glamgest.app.application.service.user;

import com.glamgest.app.application.usecase.user.DeleteUserUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Integer id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}