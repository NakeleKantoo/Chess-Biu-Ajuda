package com.chess.domain.exception.user;

import java.util.UUID;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final UUID userId;

    public UserNotFoundException(UUID userId) {
        super("Usuário não encontrado com ID: " + userId);
        this.userId = userId;
    }
    
}
