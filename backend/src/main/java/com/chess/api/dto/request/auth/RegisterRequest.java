package com.chess.api.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "O nome de usuário é obrigatório")
    String username,
    @NotBlank(message = "A senha é obrigatória")
    String password,
    @NotBlank(message = "A confirmação de senha é obrigatória")
    String confirmPassword
) {}
