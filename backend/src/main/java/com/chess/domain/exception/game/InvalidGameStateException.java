package com.chess.domain.exception.game;

import java.util.UUID;

import com.chess.domain.model.game.GameState;

import lombok.Getter;

@Getter
public class InvalidGameStateException extends RuntimeException {
    private final UUID gameId;
    private final GameState currentState;

    public InvalidGameStateException(String message, UUID gameId, GameState currentState) {
        super(String.format("%s (ID: %s, Estado: %s)", message, gameId.toString(), currentState));
        this.gameId = gameId;
        this.currentState = currentState;
    }

}
