package com.chess.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chess.api.dto.request.auth.LoginRequest;
import com.chess.api.dto.response.auth.LoginResponse;
import com.chess.app.service.auth.AuthService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse jwtResponseDTO = authService.login(
                loginRequest.username(),
                loginRequest.password());

        return ResponseEntity.ok(jwtResponseDTO);
    } 

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " do início do token, se presente
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        LoginResponse jwtResponseDTO = authService.authenticateUser(token);
        return ResponseEntity.ok(jwtResponseDTO);
    }

}
