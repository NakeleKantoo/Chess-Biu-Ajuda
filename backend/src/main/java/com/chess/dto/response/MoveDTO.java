package com.chess.dto.response;

public record MoveDTO(
    String san,
    String uci
) {}
