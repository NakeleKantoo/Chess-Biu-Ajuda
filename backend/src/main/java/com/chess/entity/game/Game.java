package com.chess.entity.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.chess.entity.base.Color;
import com.chess.entity.board.Board;
import com.chess.entity.board.BoardState;
import com.chess.entity.clock.ChessClock;
import com.chess.entity.game.GameConfig.GameType;
import com.chess.entity.move.Move;

public class Game {
    
    // Histórico completo dos estados do tabuleiro.
    // O último elemento é sempre o estado atual.
    private final List<Board> boardHistory;

    // Histórico de movimentos que levaram aos estados.
    private final List<Move> moveHistory;

    // Relógio de xadrez associado ao jogo.
    private final ChessClock chessClock;

    // Estado do jogo (ativo, vitória, empate, etc.) pode ser inferido do estado atual do tabuleiro e do relógio.
    private GameState gameState;
    private GameEndReason gameEndReason;

    private final GameType gameType; // Útil para validar roque no Chess960, por exemplo.

    // Construtor privado para forçar uso dos métodos de fábrica
    protected Game(Board initialBoard, ChessClock chessClock, GameType gameType) {
        this.boardHistory = new ArrayList<>();
        this.moveHistory = new ArrayList<>();

        this.boardHistory.add(initialBoard);
        this.chessClock = chessClock;

        this.gameState = GameState.fromBoardState(initialBoard.getBoardState(), initialBoard.getCurrentPlayer());
        this.gameEndReason = GameEndReason.fromBoardState(initialBoard.getBoardState());

        this.gameType = gameType;
    }

    public Board getCurrentBoard() { return boardHistory.getLast(); }
    public BoardState getCurrentState() { return getCurrentBoard().getBoardState(); }
    public List<Move> getMoveHistory() { return Collections.unmodifiableList(moveHistory); }
    public List<Board> getBoardHistory() { return Collections.unmodifiableList(boardHistory); }
    public long getTimeRemaining(Color color) { return chessClock.getTimeRemaining(color); }
    public long getLastMoveTimestamp() { return chessClock.getLastMoveTimestamp(); }
    public boolean isClockRunning() { return chessClock.isRunning(); }
    public GameState getGameState() { return gameState; }
    public GameEndReason getGameEndReason() { return gameEndReason; }
    public GameType getGameType() { return gameType; }

    public void startClock() { chessClock.start(); }
    public void stopClock() { chessClock.stop(); }

    /**
     * Registra um movimento e o novo estado resultante.
     * <p>
     * Este método assume que o movimento e o tabuleiro resultante já foram calculados 
     * e validados por um serviço externo (MoveValidator/MoveExecutor).
     * 
     * @param move O movimento realizado (recibo).
     * @param nextBoard O novo estado do tabuleiro após o movimento.
     */
    public void commitMove(Move move, Board nextBoard) {
        Objects.requireNonNull(move, "O movimento não pode ser nulo");
        Objects.requireNonNull(nextBoard, "O novo tabuleiro não pode ser nulo");

        this.moveHistory.add(move);
        this.boardHistory.add(nextBoard);

        this.gameState = GameState.fromBoardState(nextBoard.getBoardState(), nextBoard.getCurrentPlayer());
        this.gameEndReason = GameEndReason.fromBoardState(nextBoard.getBoardState());

        this.chessClock.start(); // Idempotente: só inicia se não estiver rodando

        if (this.gameState != GameState.ACTIVE) {
            // Se acabou (Mate/Empate), para o relógio imediatamente!
            this.chessClock.stop();
        } else {
            // Se continua, troca o turno e aplica incremento
            this.chessClock.makeMove();
        }
    }

    public void resign(Color resigningPlayer) {
        Objects.requireNonNull(resigningPlayer, "O jogador que desiste não pode ser nulo.");

        if (gameState != GameState.ACTIVE) {
            throw new IllegalStateException("O jogo já acabou. Não é possível desistir.");
        }

        GameState gameState = resigningPlayer.isWhite() ? GameState.BLACK_WON : GameState.WHITE_WON;

        endGame(gameState, GameEndReason.RESIGNATION);
    }

    public void drawByAgreement() {
        if (gameState != GameState.ACTIVE) {
            throw new IllegalStateException("O jogo já acabou. Não é possível aceitar empate.");
        }

        endGame(GameState.DRAW, GameEndReason.AGREED_DRAW);
    }

    public void timeoutDraw() {
        if (gameState != GameState.ACTIVE) {
            throw new IllegalStateException("O jogo já acabou. Não é possível declarar empate por timeout.");
        }

        endGame(GameState.DRAW, GameEndReason.TIMEOUT);
    }

    public void timeoutLoss(Color losingPlayer) {
        Objects.requireNonNull(losingPlayer, "O jogador que perdeu por timeout não pode ser nulo.");

        if (gameState != GameState.ACTIVE) {
            throw new IllegalStateException("O jogo já acabou. Não é possível declarar vitória por timeout.");
        }

        GameState gameState = losingPlayer.isWhite() ? GameState.BLACK_WON : GameState.WHITE_WON;

        endGame(gameState, GameEndReason.TIMEOUT);
    }

    public void abort() {
        if (gameState != GameState.ACTIVE) {
            throw new IllegalStateException("O jogo já acabou. Não é possível abortar.");
        }

        endGame(GameState.ABORTED, GameEndReason.ABORTION);
    }

    private void endGame(GameState endState, GameEndReason reason) {
        this.chessClock.stop();
        this.gameState = endState;
        this.gameEndReason = reason;
    }

}