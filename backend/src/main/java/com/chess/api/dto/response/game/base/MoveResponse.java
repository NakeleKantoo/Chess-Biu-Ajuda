package com.chess.api.dto.response.game.base;

public record MoveResponse(
    String san,
    String uci
) {}
