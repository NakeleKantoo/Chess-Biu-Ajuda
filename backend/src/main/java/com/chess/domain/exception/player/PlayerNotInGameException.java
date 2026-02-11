package com.chess.domain.exception.player;

import java.util.UUID;

import lombok.Getter;

@Getter
public class PlayerNotInGameException extends RuntimeException {
    
    private final UUID playerId;

    public PlayerNotInGameException(UUID playerId) {
        super("O jogador com ID: " + playerId + " não está participando deste jogo.");
        this.playerId = playerId;
    }

}
