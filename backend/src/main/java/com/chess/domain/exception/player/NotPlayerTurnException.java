package com.chess.domain.exception.player;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class NotPlayerTurnException extends ChessException {

    private final UUID playerId;

    public NotPlayerTurnException(UUID playerId) {
        super("Não é a vez do jogador com ID: " + playerId);
        this.playerId = playerId;
    }

    @Override public String getType() { return "Not Player Turn"; }
    @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("playerId", playerId);
    }
    
}
