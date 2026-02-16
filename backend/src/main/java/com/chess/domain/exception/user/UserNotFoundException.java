package com.chess.domain.exception.user;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class UserNotFoundException extends ChessException {
    private final UUID userId;

    public UserNotFoundException(UUID userId) {
        super("Usuário não encontrado com ID: " + userId);
        this.userId = userId;
    }

    @Override public String getType() { return "User Not Found"; }
    @Override public HttpStatus getStatus() { return HttpStatus.NOT_FOUND; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("gameId", userId);
    }
    
}
