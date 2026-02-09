package com.chess.domain.model.game;

import java.util.UUID;

public record PendingGame(
    UUID gameId,
    UUID creatorId,
    GameConfig config
) {}
