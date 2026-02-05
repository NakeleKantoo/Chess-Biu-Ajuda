package com.chess.entity.game;

import java.util.Objects;

import com.chess.entity.board.Board;
import com.chess.entity.board.BoardBuilder;
import com.chess.entity.clock.ChessClock;
import com.chess.entity.clock.ChessClockBuilder;

public class GameBuilder {

    // Configurações padrão
    private Board initialBoard;
    private ChessClock chessClock;

    public GameBuilder() {
        // Valores default (Xadrez Padrão, 10 min)
        this.initialBoard = new BoardBuilder().buildStandard();
        this.chessClock = ChessClockBuilder.RAPID();
    }

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

    public static Game standardClassicalGame() {
        return new GameBuilder()
            .withBoard(new BoardBuilder().buildStandard())
            .withClock(ChessClockBuilder.CLASSICAL())
            .build();
    }

    public static Game standardRapidGame() {
        return new GameBuilder()
            .withBoard(new BoardBuilder().buildStandard())
            .withClock(ChessClockBuilder.RAPID())
            .build();
    }

    public static Game standardBlitzGame() {
        return new GameBuilder()
            .withBoard(new BoardBuilder().buildStandard())
            .withClock(ChessClockBuilder.BLITZ())
            .build();
    }

    public static Game standardBulletGame() {
        return new GameBuilder()
            .withBoard(new BoardBuilder().buildStandard())
            .withClock(ChessClockBuilder.BULLET())
            .build();
    }

    // --- Build Final ---

    public Game build() {
        Objects.requireNonNull(initialBoard, "O tabuleiro inicial não pode ser nulo.");
        Objects.requireNonNull(chessClock, "O relógio de xadrez não pode ser nulo.");
        // Aqui chamamos um construtor pacote-privado ou público do Game
        return new Game(initialBoard, chessClock);
    }
    
}
