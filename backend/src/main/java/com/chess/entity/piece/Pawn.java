package com.chess.entity.piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.chess.entity.base.*;
import com.chess.entity.board.BaseBoard;

public class Pawn extends Piece {

    // A direção para a qual este peão avança (um passo ou dois)
    private final Direction forwardDirection;
    // As direções para as quais este peão captura
    private final Direction[] captureDirections;
    // Posição inicial no tabuleiro
    private final int initialRow;

    protected Pawn(Color color) {
        super(color);

        if (color == Color.WHITE) {
            // Peões brancos se movem "para cima" no tabuleiro (índice da linha diminui)
            this.forwardDirection = Direction.UP;
            this.captureDirections = new Direction[]{Direction.UP_LEFT, Direction.UP_RIGHT};
            this.initialRow = 6;
        } else {
            // Peões pretos se movem "para baixo" no tabuleiro (índice da linha aumenta)
            this.forwardDirection = Direction.DOWN;
            this.captureDirections = new Direction[]{Direction.DOWN_LEFT, Direction.DOWN_RIGHT};
            this.initialRow = 1;
        }
    }

    private boolean isAtInitialPosition(Position from) {
        return from.getRow() == initialRow;
    }

    private boolean isCaptureDirection(Direction dir) {
        if (dir == null) return false;

        // Verifica se a direção está entre as direções de captura
        for (Direction captureDir : captureDirections) {
            if (captureDir == dir) return true;
        }

        return false;
    }

    private List<Position> getCaptureMoves(BaseBoard board, Position from) {
        List<Position> captureMoves = new ArrayList<>();

        // Verifica cada direção de captura
        for (Direction dir : captureDirections) {
            Position to = from.getNext(dir);
            Color oppositeColor = this.color.opposite();

            // Verifica se a posição de destino é válida
            if (to == null) continue; 
            
            // Adiciona o movimento de captura se houver uma peça adversária na posição de destino ou se for um movimento de en passant
            if (board.hasPieceAt(to, oppositeColor) || to.equals(board.getEnPassantTarget())) {
                captureMoves.add(to);
            }
        }
        return captureMoves;
    }

    private List<Position> getForwardMoves(BaseBoard board, Position from) {
        List<Position> forwardMoves = new ArrayList<>();

        // Verifica a quantidade de passos possíveis (1 ou 2)
        int step = isAtInitialPosition(from) ? 2 : 1;

        for (int i = 0; i < step; i++) {
            Position to = from.getNext(forwardDirection);

            // Verifica se a posição de destino é válida
            if (to == null) break; 

            // Adiciona o movimento se a posição de destino estiver vazia
            if (!board.hasPieceAt(to)) {
                forwardMoves.add(to);
                from = to; // Atualiza a posição "from" para o próximo passo
            } else {
                break; // Interrompe se encontrar uma peça bloqueando o caminho
            }
        }

        return forwardMoves;
    }

    @Override
    public char getSymbol() {
        return this.color.isWhite() ? 'P' : 'p';
    }

    @Override
    public boolean isAttacking(BaseBoard board, Position from, Position to) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "A posição de destino não pode ser nula.");

        Direction dir = Direction.get(from, to);
        // Verifica se a posição é adjacente e na direção de captura
        boolean isAttacking = isCaptureDirection(dir) && from.isNear(to);

        return isAttacking;
    }

    @Override
    public List<Position> getPossibleMoves(BaseBoard board, Position from) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");

        List<Position> moves = new ArrayList<>();

        // Adiciona movimentos para frente e capturas
        moves.addAll(getForwardMoves(board, from));
        moves.addAll(getCaptureMoves(board, from));

        return moves;
    }

}
