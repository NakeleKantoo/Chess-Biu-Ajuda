package com.chess.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chess.dto.request.RegisterDTO;
import com.chess.dto.response.JwtResponseDTO;
import com.chess.entity.user.User;
import com.chess.service.auth.AuthService;
import com.chess.service.user.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    AuthService authService;
    
    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterDTO registerDTO) {
        if (!registerDTO.password().equals(registerDTO.confirmPassword())) {
            return ResponseEntity.badRequest().body("As senhas não coincidem.");
        }

        try {
            // 1. Cria o usuário no banco
            userService.createUser(registerDTO.username(), registerDTO.password());

            // 2. Autentica automaticamente (Auto-Login)
            JwtResponseDTO jwtResponseDTO = authService.login(
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
