package com.chess.domain.exception.game;

import java.util.UUID;

import lombok.Getter;

@Getter
public class DuplicateGameException extends RuntimeException {
    
    private final UUID gameId;

    public DuplicateGameException(UUID gameId) {
        super("Já existe um jogo salvo com ID: " + gameId);
        this.gameId = gameId;
    }

}
