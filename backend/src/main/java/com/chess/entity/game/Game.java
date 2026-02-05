package com.chess.entity.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.chess.entity.base.Color;
import com.chess.entity.board.Board;
import com.chess.entity.board.BoardState;
import com.chess.entity.clock.ChessClock;
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

    // Construtor privado para forçar uso dos métodos de fábrica
    protected Game(Board initialBoard, ChessClock chessClock) {
        this.boardHistory = new ArrayList<>();
        this.moveHistory = new ArrayList<>();

        this.boardHistory.add(initialBoard);
        this.chessClock = chessClock;

        this.gameState = GameState.fromBoardState(initialBoard.getBoardState(), initialBoard.getCurrentPlayer());
        this.gameEndReason = GameEndReason.fromBoardState(initialBoard.getBoardState());
    }

    public Board getCurrentBoard() { return boardHistory.getLast(); }
    public BoardState getCurrentState() { return getCurrentBoard().getBoardState(); }
    public List<Move> getMoveHistory() { return Collections.unmodifiableList(moveHistory); }
    public List<Board> getBoardHistory() { return Collections.unmodifiableList(boardHistory); }
    public long getTimeRemaining(Color color) { return chessClock.getTimeRemaining(color); }
    public GameState getGameState() { return gameState; }
    public GameEndReason getGameEndReason() { return gameEndReason; }

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

    /**
     * Desfaz o último movimento, revertendo o jogo para o estado anterior.
     * Retorna o movimento que foi desfeito.
     */
    public Move undoLastMove() {
        if (moveHistory.isEmpty()) {
            return null;
        }

        Move lastMove = moveHistory.remove(moveHistory.size() - 1);
        boardHistory.remove(boardHistory.size() - 1);
        chessClock.undoMove();
        return lastMove;
    }
}