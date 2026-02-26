package com.chess.api.dto.response.game.player;

import java.util.UUID;

public record PlayerDTO(
    UUID id,
    String name
) {}