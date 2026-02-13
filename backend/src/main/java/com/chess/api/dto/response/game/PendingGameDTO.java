package com.chess.api.dto.response.game;

import java.util.UUID;

import com.chess.domain.model.game.PendingGame;

public record PendingGameDTO(
    UUID gameId,
    String gameCode
) {
    public PendingGameDTO(PendingGame pendingGame) {
        this(pendingGame.gameId(), pendingGame.gameCode());
    }
}
