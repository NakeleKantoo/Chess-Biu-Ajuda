package com.chess.api.dto.response.game;

import java.util.UUID;

public record PendingGameDTO(
    UUID gameId,
    String gameCode
) {}
