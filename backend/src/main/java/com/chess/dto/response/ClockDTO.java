package com.chess.dto.response;

public record ClockDTO(
    long whiteTimeRemaining,
    long blackTimeRemaining,

    long lastMoveTimestamp,
    boolean isRunning
) {}
