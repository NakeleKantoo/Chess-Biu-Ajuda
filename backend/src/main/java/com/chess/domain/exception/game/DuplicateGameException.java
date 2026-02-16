package com.chess.domain.exception.game;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class DuplicateGameException extends ChessException {
    
    private final UUID gameId;

    public DuplicateGameException(UUID gameId) {
        super("Já existe um jogo salvo com ID: " + gameId);
        this.gameId = gameId;
    }

    @Override public String getType() { return "Duplicate Game"; }
    @Override public HttpStatus getStatus() { return HttpStatus.CONFLICT; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("gameId", gameId);
    }

}
