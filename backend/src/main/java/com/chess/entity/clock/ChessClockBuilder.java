package com.chess.entity.clock;

import com.chess.entity.base.Color;

public class ChessClockBuilder {

    private static final long BULLET_1M = 1 * 60 * 1000;
    private static final long BULLET_2M = 2 * 60 * 1000;

    private static final long BLITZ_3M = 3 * 60 * 1000;
    private static final long BLITZ_5M = 5 * 60 * 1000;
    
    private static final long RAPID_10M = 10 * 60 * 1000;
    private static final long RAPID_15M = 15 * 60 * 1000;

    private static final long CLASSICAL_30M = 30 * 60 * 1000;
    private static final long CLASSICAL_60M = 60 * 60 * 1000;

    public static ChessClock BULLET_1M() {
        return BULLET_1M(0);
    }
    public static ChessClock BULLET_1M(long incrementMillis) {
        return new ChessClock(BULLET_1M, incrementMillis, Color.WHITE);
    }
    public static ChessClock BULLET_2M() {
        return BULLET_2M(0);
    }
    public static ChessClock BULLET_2M(long incrementMillis) {
        return new ChessClock(BULLET_2M, incrementMillis, Color.WHITE);
    }
    public static ChessClock BLITZ_3M() {
        return BLITZ_3M(0);
    }
    public static ChessClock BLITZ_3M(long incrementMillis) {
        return new ChessClock(BLITZ_3M, incrementMillis, Color.WHITE);
    }
    public static ChessClock BLITZ_5M() {
        return BLITZ_5M(0);
    }
    public static ChessClock BLITZ_5M(long incrementMillis) {
        return new ChessClock(BLITZ_5M, incrementMillis, Color.WHITE);
    }
    public static ChessClock RAPID_10M() {
        return RAPID_10M(0);
    }
    public static ChessClock RAPID_10M(long incrementMillis) {
        return new ChessClock(RAPID_10M, incrementMillis, Color.WHITE);
    }
    public static ChessClock RAPID_15M() {
        return RAPID_15M(0);
    }
    public static ChessClock RAPID_15M(long incrementMillis) {
        return new ChessClock(RAPID_15M, incrementMillis, Color.WHITE);
    }
    public static ChessClock CLASSICAL_30M() {
        return CLASSICAL_30M(0);
    }
    public static ChessClock CLASSICAL_30M(long incrementMillis) {
        return new ChessClock(CLASSICAL_30M, incrementMillis, Color.WHITE);
    }
    public static ChessClock CLASSICAL_60M() {
        return CLASSICAL_60M(0);
    }
    public static ChessClock CLASSICAL_60M(long incrementMillis) {
        return new ChessClock(CLASSICAL_60M, incrementMillis, Color.WHITE);
    }
    
}
