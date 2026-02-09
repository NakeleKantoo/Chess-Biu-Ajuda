package com.chess.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chess.dto.request.LoginRequestDTO;
import com.chess.dto.response.JwtResponseDTO;
import com.chess.service.auth.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        JwtResponseDTO jwtResponseDTO = authService.login(
                loginRequest.username(),
                loginRequest.password());

        return ResponseEntity.ok(jwtResponseDTO);
    } 

}
