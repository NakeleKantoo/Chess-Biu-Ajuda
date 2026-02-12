package com.chess.domain.exception.player;

import java.util.UUID;

import lombok.Getter;

@Getter
public class NotPlayerTurnException extends RuntimeException {

    private final UUID playerId;

    public NotPlayerTurnException(UUID playerId) {
        super("Não é a vez do jogador com ID: " + playerId);
        this.playerId = playerId;
    }
    
}
