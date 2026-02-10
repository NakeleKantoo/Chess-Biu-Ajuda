package com.chess.api.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String confirmPassword
) {}
