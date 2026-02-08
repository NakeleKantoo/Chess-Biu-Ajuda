package com.chess.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String confirmPassword
) {}
