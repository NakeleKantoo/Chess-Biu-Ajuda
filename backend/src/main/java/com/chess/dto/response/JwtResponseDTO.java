package com.chess.dto.response;

import com.chess.entity.user.Role;

public record JwtResponseDTO(
    String accessToken,
    String username,
    Role role,
    String tokenType
) {
    public JwtResponseDTO(String accessToken, String username, Role role) {
        this(accessToken, username, role, "Bearer");
    }
}