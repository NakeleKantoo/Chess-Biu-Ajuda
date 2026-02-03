package com.chess.entity.piece;

import java.util.List;
import java.util.Objects;

import com.chess.entity.base.*;
import com.chess.entity.board.BaseBoard;
import com.chess.entity.board.BoardAnalyzer;
import com.chess.utils.MoveUtils;

public class Bishop extends Piece {

    protected Bishop(Color color) {
        super(color);
    }

    @Override
    public char getSymbol() {
        return this.color.isWhite() ? 'B' : 'b';
    }
    
    @Override
    public boolean isAttacking(BaseBoard board, Position from, Position to) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "A posição de destino não pode ser nula.");

        Direction dir = Direction.get(from, to);

        if (dir == null) return false;

        // Verifica se o caminho está livre de peças e se está na direção diagonal
        boolean isAttacking = dir.isDiagonal() && BoardAnalyzer.isPathClear(board, from, to);

        return isAttacking;
    }
    
    @Override
    public List<Position> getPossibleMoves(BaseBoard board, Position from) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");

        Direction[] directions = Direction.getDiagonalDirections();

        return MoveUtils.getSlidingMoves(board, this.color, from, directions);
    }

}
