package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.UserRequestDTO;
import com.glamgest.app.application.dto.UserResponseDTO;
import com.glamgest.app.application.dto.UserUpdateDTO;
import com.glamgest.app.application.usecase.*;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;

    public UserController(GetUserByIdUseCase getUserByIdUseCase,
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase,
            GetAllUsersUseCase getAllUsersUseCase) {
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return GlobalRegistrationExceptionHandler.buildResponse(getUserByIdUseCase.execute(id), "usuarios obtenidos", HttpStatus.OK, true);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO,
            BindingResult result) {
        if (result.hasFieldErrors()) {
            return this.validation(result);
        }

        UserResponseDTO createdUser = createUserUseCase.execute(userRequestDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer id,
            @RequestBody UserUpdateDTO userUpdateDTO) {
        userUpdateDTO.setId(id);
        UserResponseDTO updatedUser = updateUserUseCase.execute(userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = getAllUsersUseCase.execute();
        return ResponseEntity.ok(users);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}