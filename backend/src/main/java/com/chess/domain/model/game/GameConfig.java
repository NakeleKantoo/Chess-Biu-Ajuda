package com.chess.domain.model.game;

import java.util.Objects;

import com.chess.domain.model.base.Color;

import lombok.Getter;

@Getter
public class GameConfig {

    private final TimeControl timeControl;
    private final GameType gameType;
    private final Color startingColor;
    private final Color creatorColor;

    private final Long whiteTimeRemaining;
    private final Long blackTimeRemaining;
    private final Long incrementMillis;

    public static GameConfig custom(GameType gameType, Long whiteTimeRemaining, Long blackTimeRemaining, Long incrementMillis, Color startingColor, Color creatorColor) {
        Objects.requireNonNull(gameType, "O tipo de jogo não pode ser nulo.");
        Objects.requireNonNull(whiteTimeRemaining, "O tempo para as brancas não pode ser nulo.");
        Objects.requireNonNull(blackTimeRemaining, "O tempo para as pretas não pode ser nulo.");
        Objects.requireNonNull(incrementMillis, "O incremento de tempo não pode ser nulo.");
        Objects.requireNonNull(startingColor, "A cor inicial não pode ser nula.");
        
        return new GameConfig(TimeControl.CUSTOM, gameType, startingColor, creatorColor, whiteTimeRemaining, blackTimeRemaining, incrementMillis);
    }

    public static GameConfig of(TimeControl timeControl, GameType gameType, Color startingColor, Color creatorColor) {
        Objects.requireNonNull(timeControl, "O controle de tempo não pode ser nulo.");
        Objects.requireNonNull(gameType, "O tipo de jogo não pode ser nulo.");
        Objects.requireNonNull(startingColor, "A cor inicial não pode ser nula.");
        
        return switch (timeControl) {
            case BULLET -> new GameConfig(timeControl, gameType, startingColor, creatorColor,null, null, null); // Os tempos serão definidos pelo ChessClockBuilder
            case BLITZ -> new GameConfig(timeControl, gameType, startingColor, creatorColor,null, null, null);
            case RAPID -> new GameConfig(timeControl, gameType, startingColor, creatorColor,null, null, null);
            case CLASSICAL -> new GameConfig(timeControl, gameType, startingColor, creatorColor,null, null, null);
            case CUSTOM -> throw new IllegalArgumentException("Para o controle de tempo CUSTOM, use o método custom.");
        };
    }

    public static GameConfig of(TimeControl timeControl, GameType gameType) {
        return of(timeControl, gameType, Color.WHITE, Color.WHITE);
    }

    private GameConfig(TimeControl timeControl, GameType gameType, Color startingColor, Color creatorColor, Long whiteTimeRemaining, Long blackTimeRemaining, Long incrementMillis) {
        this.timeControl = timeControl;
        this.gameType = gameType;
        this.startingColor = startingColor;
        this.creatorColor = creatorColor;

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
    }
    
    public enum GameType {
        STANDARD,
        CHESS960;
    }

}
