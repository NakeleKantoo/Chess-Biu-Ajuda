package com.chess.api.dto.response.game;

public record MoveDTO(
    String san,
    String uci
) {}
