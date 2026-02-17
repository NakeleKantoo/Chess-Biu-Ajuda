package com.chess.domain.exception.user;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class DuplicateUsernameException extends ChessException {

    private final String username;

    public DuplicateUsernameException(String username) {
        super("Já existe um usuário com o nick: " + username);
        this.username = username;
    }

    @Override public String getType() { return "Duplicate Username"; }
    @Override public HttpStatus getStatus() { return HttpStatus.CONFLICT; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("gameId", username);
    }
    
}
