package com.chess.domain.model.clock;

import java.util.Objects;

import com.chess.domain.model.base.Color;
import com.chess.domain.model.game.GameConfig.TimeControl;

public class ChessClockBuilder {

    private long whiteTimeMillis;
    private long blackTimeMillis;
    private long incrementMillis;
    private Color startingColor;

    private static final long BULLET = 1 * 60 * 1000;
    private static final long BLITZ = 3 * 60 * 1000;
    private static final long RAPID = 10 * 60 * 1000;
    private static final long CLASSICAL = 30 * 60 * 1000;

    public static ChessClock buildByTimeControl(TimeControl timeControl, Color startingColor) {
        switch (timeControl) {
            case BULLET:
                return new ChessClock(BULLET, 0, startingColor);
            case BLITZ:
                return new ChessClock(BLITZ, 0, startingColor);
            case RAPID:
                return new ChessClock(RAPID, 0, startingColor);
            case CLASSICAL:
                return new ChessClock(CLASSICAL, 0, startingColor);
            case CUSTOM:
                throw new IllegalArgumentException("Para o controle de tempo CUSTOM, use os métodos específicos do ChessClockBuilder.");
        }
        return new ChessClock(BULLET, 0, Color.WHITE);
    }

    public ChessClockBuilder withWhiteTime(long whiteTimeMillis) {
        Objects.requireNonNull(whiteTimeMillis, "O tempo para as brancas não pode ser nulo.");
        this.whiteTimeMillis = whiteTimeMillis;
        return this;
    }

    public ChessClockBuilder withBlackTime(long blackTimeMillis) {
        Objects.requireNonNull(blackTimeMillis, "O tempo para as pretas não pode ser nulo.");
        this.blackTimeMillis = blackTimeMillis;
        return this;
    }

    public ChessClockBuilder withIncrement(long incrementMillis) {
        Objects.requireNonNull(incrementMillis, "O incremento de tempo não pode ser nulo.");
        this.incrementMillis = incrementMillis;
        return this;
    }

    public ChessClockBuilder withStartingColor(Color startingColor) {
        Objects.requireNonNull(startingColor, "A cor inicial não pode ser nula.");
        this.startingColor = startingColor;
        return this;
    }

    public ChessClock build() {
        Objects.requireNonNull(startingColor, "A cor inicial deve ser definida.");
        if (whiteTimeMillis <= 0 || blackTimeMillis <= 0) {
            throw new IllegalArgumentException("O tempo para ambos os jogadores deve ser maior que zero.");
        }
        if (incrementMillis < 0) {
            throw new IllegalArgumentException("O incremento de tempo não pode ser negativo.");
        }

        return new ChessClock(whiteTimeMillis, blackTimeMillis, incrementMillis, startingColor);
    }

}
