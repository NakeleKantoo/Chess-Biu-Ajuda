package com.chess.domain.model.game;

import java.util.Objects;

import com.chess.domain.model.base.Color;
import com.chess.domain.model.board.Board;
import com.chess.domain.model.board.BoardBuilder;
import com.chess.domain.model.clock.ChessClock;
import com.chess.domain.model.clock.ChessClockBuilder;
import com.chess.domain.model.game.GameConfig.GameType;
import com.chess.domain.model.game.GameConfig.TimeControl;

public class GameBuilder {

    // Configurações padrão
    private Board initialBoard;
    private ChessClock chessClock;
    private GameType gameType;

    public GameBuilder() {}

    // Define um tabuleiro específico
    public GameBuilder withBoard(Board board) {
        Objects.requireNonNull(board, "O tabuleiro inicial não pode ser nulo.");
        this.initialBoard = board;
        return this;
    }

    // Define um relógio específico
    public GameBuilder withClock(ChessClock clock) {
        Objects.requireNonNull(clock, "O relógio de xadrez não pode ser nulo.");
        this.chessClock = clock;
        return this;
    }

    public GameBuilder withGameType(GameType gameType) {
        Objects.requireNonNull(gameType, "O tipo de jogo não pode ser nulo.");
        this.gameType = gameType;
        return this;
    }

    public static Game from(GameConfig config) {
        Objects.requireNonNull(config, "A configuração do jogo não pode ser nula.");

        GameBuilder builder = new GameBuilder();

        // Configura o tabuleiro com base no tipo de jogo
        builder.withBoard(BoardBuilder.buildByGameType(config.getGameType()));
        builder.withGameType(config.getGameType());

        // Configura o relógio com base no tipo de jogo
        if (config.getTimeControl() != TimeControl.CUSTOM) {
            builder.withClock(ChessClockBuilder.buildByTimeControl(config.getTimeControl(), config.getStartingColor()));
        } else {
            builder.withClock(new ChessClockBuilder()
                .withWhiteTime(config.getWhiteTimeRemaining())
                .withBlackTime(config.getBlackTimeRemaining())
                .withIncrement(config.getIncrementMillis())
                .withStartingColor(config.getStartingColor())
                .build());
        }

        return builder.build();
    }

    public static Game create(GameType gameType, TimeControl timeControl, Color startingColor) {
        return new GameBuilder()
            .withBoard(BoardBuilder.buildByGameType(gameType))
            .withClock(ChessClockBuilder.buildByTimeControl(timeControl, startingColor))
            .withGameType(gameType)
            .build();
    }

    // --- Build Final ---

    public Game build() {
        Objects.requireNonNull(initialBoard, "O tabuleiro inicial não pode ser nulo.");
        Objects.requireNonNull(chessClock, "O relógio de xadrez não pode ser nulo.");
        Objects.requireNonNull(gameType, "O tipo de jogo não pode ser nulo.");
        // Aqui chamamos um construtor pacote-privado ou público do Game
        return new Game(initialBoard, chessClock, gameType);
    }
    
}
