package com.chess.domain.exception.player;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class PlayerNotInGameException extends ChessException {
    
    private final UUID playerId;

    public PlayerNotInGameException(UUID playerId) {
        super("O jogador com ID: " + playerId + " não está participando deste jogo.");
        this.playerId = playerId;
    }

    @Override public String getType() { return "Player Not In Game"; }
    @Override public HttpStatus getStatus() { return HttpStatus.FORBIDDEN; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("playerId", playerId);
    }

}
