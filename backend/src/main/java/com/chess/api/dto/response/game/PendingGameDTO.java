package com.chess.api.dto.response.game;

import java.util.UUID;

import com.chess.domain.model.game.GameConfig;
import com.chess.domain.model.game.PendingGame;

public record PendingGameDTO(
    UUID gameId,
    String gameCode,
    GameConfig config
) {
    public PendingGameDTO(PendingGame pendingGame) {
        this(pendingGame.gameId(), pendingGame.gameCode(), pendingGame.config());
    }
}
