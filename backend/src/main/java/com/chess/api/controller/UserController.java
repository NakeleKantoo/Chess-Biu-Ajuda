package com.chess.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chess.api.dto.request.auth.RegisterRequest;
import com.chess.api.dto.response.auth.LoginResponse;
import com.chess.app.service.auth.AuthService;
import com.chess.app.service.user.UserService;
import com.chess.domain.model.user.User;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    AuthService authService;
    
    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest registerDTO) {
        if (!registerDTO.password().equals(registerDTO.confirmPassword())) {
            return ResponseEntity.badRequest().body("As senhas não coincidem.");
        }

        try {
            // 1. Cria o usuário no banco
            userService.createUser(registerDTO.username(), registerDTO.password());

            // 2. Autentica automaticamente (Auto-Login)
            LoginResponse jwtResponseDTO = authService.login(
                    registerDTO.username(),
                    registerDTO.password());

            return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
