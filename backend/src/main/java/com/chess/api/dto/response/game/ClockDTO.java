package com.chess.api.dto.response.game;

public record ClockDTO(
    long whiteTimeRemaining,
    long blackTimeRemaining,

    long lastMoveTimestamp,
    boolean isRunning
) {}
