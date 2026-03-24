package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.UserResponseDTO;
import com.glamgest.app.application.usecase.GetUserByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final GetUserByIdUseCase getUserByIdUseCase;

    public UserController(GetUserByIdUseCase getUserByIdUseCase) {
        this.getUserByIdUseCase = getUserByIdUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Integer id) {

        return ResponseEntity.ok(
                getUserByIdUseCase.execute(id));
    }
}