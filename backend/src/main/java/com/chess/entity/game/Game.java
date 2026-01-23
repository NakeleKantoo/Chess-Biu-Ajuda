package com.chess.entity.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.chess.entity.board.Board;
import com.chess.entity.board.BoardBuilder;
import com.chess.entity.board.BoardState;

public class Game {
    
    // Histórico completo dos estados do tabuleiro.
    // O último elemento é sempre o estado atual.
    private final List<Board> boardHistory;

    // Histórico de movimentos que levaram aos estados.
    private final List<Move> moveHistory;

    // Construtor privado para forçar uso dos métodos de fábrica
    private Game(Board initialBoard) {
        this.boardHistory = new ArrayList<>();
        this.moveHistory = new ArrayList<>();
        this.boardHistory.add(initialBoard);
    }

    /**
     * Inicia um novo jogo com as regras e posições padrão.
     */
    public static Game startNewStandardGame() {
        Board initialBoard = new BoardBuilder().buildStandard();
        return new Game(initialBoard);
    }

    /**
     * Inicia um jogo a partir de um tabuleiro personalizado (ex: para cenários de teste).
     */
    public static Game startFromBoard(Board board) {
        Objects.requireNonNull(board, "O tabuleiro inicial não pode ser nulo.");
        return new Game(board);
    }

    public Board getCurrentBoard() { return boardHistory.getLast(); }
    public BoardState getCurrentState() { return getCurrentBoard().getBoardState(); }
    public List<Move> getMoveHistory() { return Collections.unmodifiableList(moveHistory); }
    public List<Board> getBoardHistory() { return Collections.unmodifiableList(boardHistory); }

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
        
        return lastMove;
    }
}