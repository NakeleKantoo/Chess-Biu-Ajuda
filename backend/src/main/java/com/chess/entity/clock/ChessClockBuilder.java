package com.chess.entity.clock;

import java.util.Objects;

import com.chess.entity.base.Color;

public class ChessClockBuilder {

    private static final long BULLET = 1 * 60 * 1000;
    private static final long BLITZ = 3 * 60 * 1000;
    private static final long RAPID = 10 * 60 * 1000;
    private static final long CLASSICAL = 30 * 60 * 1000;

    public static ChessClock BULLET() {
        return new ChessClock(BULLET, 0, Color.WHITE);
    }

    public static ChessClock BLITZ() {
        return new ChessClock(BLITZ, 0, Color.WHITE);
    }

    public static ChessClock RAPID() {
        return new ChessClock(RAPID, 0, Color.WHITE);
    }

    public static ChessClock CLASSICAL() {
        return new ChessClock(CLASSICAL, 0, Color.WHITE);
    }

    public static ChessClock CUSTOM(long whiteTimeRemaining, long blackTimeRemaining, long incrementMillis, Color startingColor) {
        Objects.requireNonNull(startingColor, "A cor inicial não pode ser nula.");

        if (whiteTimeRemaining < 0 || blackTimeRemaining < 0 || incrementMillis < 0) {
            throw new IllegalArgumentException("Os tempos e incrementos não podem ser negativos.");
        }

        return new ChessClock(whiteTimeRemaining, blackTimeRemaining, incrementMillis, startingColor);
    }

}
