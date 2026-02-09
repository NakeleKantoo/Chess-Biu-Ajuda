package com.chess.dto.response;

import java.util.UUID;

public record PendingGameDTO(
    UUID gameId,
    String gameCode
) {}
