package com.chess.api.dto.response.game.base;

public record MoveDTO(
    String san,
    String uci
) {}
