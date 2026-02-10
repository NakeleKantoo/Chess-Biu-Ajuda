package com.chess.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chess.api.dto.request.auth.LoginRequest;
import com.chess.api.dto.response.auth.LoginResponse;
import com.chess.app.service.auth.AuthService;

@RestController
@RequestMapping("/auth")
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

}
