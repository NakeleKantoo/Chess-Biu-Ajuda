package com.chess.domain.exception.game;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;
import com.chess.domain.model.game.GameState;

import lombok.Getter;

@Getter
public class InvalidGameStateException extends ChessException {

    private final UUID gameId;
    private final GameState currentState;

    public InvalidGameStateException(String message, UUID gameId, GameState currentState) {
        super(String.format("%s (ID: %s, Estado: %s)", message, gameId.toString(), currentState));
        this.gameId = gameId;
        this.currentState = currentState;
    }

    @Override public String getType() { return "Invalid Game State"; }
    @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("gameId", gameId, "currentState", currentState);
    }

}
