package com.chess.api.dto.response.auth;

import com.chess.domain.model.user.Role;

public record LoginResponse(
    String accessToken,
    String username,
    Role role,
    String tokenType
) {
    public LoginResponse(String accessToken, String username, Role role) {
        this(accessToken, username, role, "Bearer");
    }
}