package com.chess.entity.game;

import java.util.Objects;

import com.chess.entity.base.Color;

public class GameConfig {

    private final TimeControl timeControl;
    private final GameType gameType;
    private final Color startingColor;

    private final Long whiteTimeRemaining;
    private final Long blackTimeRemaining;
    private final Long incrementMillis;

    public static GameConfig custom(GameType gameType, Long whiteTimeRemaining, Long blackTimeRemaining, Long incrementMillis, Color startingColor) {
        Objects.requireNonNull(gameType, "O tipo de jogo não pode ser nulo.");
        Objects.requireNonNull(whiteTimeRemaining, "O tempo para as brancas não pode ser nulo.");
        Objects.requireNonNull(blackTimeRemaining, "O tempo para as pretas não pode ser nulo.");
        Objects.requireNonNull(incrementMillis, "O incremento de tempo não pode ser nulo.");
        Objects.requireNonNull(startingColor, "A cor inicial não pode ser nula.");
        
        return new GameConfig(TimeControl.CUSTOM, gameType, startingColor, whiteTimeRemaining, blackTimeRemaining, incrementMillis);
    }

    public static GameConfig of(TimeControl timeControl, GameType gameType, Color startingColor) {
        Objects.requireNonNull(timeControl, "O controle de tempo não pode ser nulo.");
        Objects.requireNonNull(gameType, "O tipo de jogo não pode ser nulo.");
        Objects.requireNonNull(startingColor, "A cor inicial não pode ser nula.");
        
        return switch (timeControl) {
            case BULLET -> new GameConfig(timeControl, gameType, startingColor, null, null, null); // Os tempos serão definidos pelo ChessClockBuilder
            case BLITZ -> new GameConfig(timeControl, gameType, startingColor, null, null, null);
            case RAPID -> new GameConfig(timeControl, gameType, startingColor, null, null, null);
            case CLASSICAL -> new GameConfig(timeControl, gameType, startingColor, null, null, null);
            case CUSTOM -> throw new IllegalArgumentException("Para o controle de tempo CUSTOM, use o método custom.");
        };
    }

    public static GameConfig of(TimeControl timeControl, GameType gameType) {
        return of(timeControl, gameType, Color.WHITE);
    }

    private GameConfig(TimeControl timeControl, GameType gameType, Color startingColor, Long whiteTimeRemaining, Long blackTimeRemaining, Long incrementMillis) {
        this.timeControl = timeControl;
        this.gameType = gameType;
        this.startingColor = startingColor;

        this.whiteTimeRemaining = whiteTimeRemaining;
        this.blackTimeRemaining = blackTimeRemaining;
        this.incrementMillis = incrementMillis;
    }

    public enum TimeControl {
        BULLET,
        BLITZ,
        RAPID,
        CLASSICAL,
        CUSTOM;

        public static TimeControl fromString(String value) {
            return switch (value.toUpperCase()) {
                case "BULLET" -> BULLET;
                case "BLITZ" -> BLITZ;
                case "RAPID" -> RAPID;
                case "CLASSICAL" -> CLASSICAL;
                case "CUSTOM" -> CUSTOM;
                default -> throw new IllegalArgumentException("Controle de tempo inválido: " + value);
            };
        }
    }
    
    public enum GameType {
        STANDARD,
        CHESS960;

        public static GameType fromString(String value) {
            return switch (value.toUpperCase()) {
                case "STANDARD" -> STANDARD;
                case "CHESS960" -> CHESS960;
                default -> throw new IllegalArgumentException("Tipo de jogo inválido: " + value);
            };
        }
    }

    public TimeControl getTimeControl() { return timeControl; }
    public GameType getGameType() { return gameType; }
    public Color getStartingColor() { return startingColor; }

    public Long getWhiteTimeRemaining() { return whiteTimeRemaining; }
    public Long getBlackTimeRemaining() { return blackTimeRemaining; }
    public Long getIncrementMillis() { return incrementMillis; }

}
