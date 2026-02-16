package com.chess.domain.exception.game;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class InvalidGameActionException extends ChessException {
    
    private final String action;

    public InvalidGameActionException(String action) {
        super("Ação inválida para jogo: " + action);
        this.action = action;
    }

    @Override public String getType() { return "Invalid Game Action"; }
    @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("action", action);
    }

}
