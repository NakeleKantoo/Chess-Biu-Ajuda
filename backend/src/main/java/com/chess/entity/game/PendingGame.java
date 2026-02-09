package com.chess.entity.game;

import java.util.UUID;

public record PendingGame(
    UUID gameId,
    UUID creatorId,
    GameConfig config
) {}
