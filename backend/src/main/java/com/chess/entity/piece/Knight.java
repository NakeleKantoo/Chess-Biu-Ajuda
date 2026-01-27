package com.chess.entity.piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.chess.entity.base.*;
import com.chess.entity.board.Board;

public class Knight extends Piece {

    private static final int[][] KNIGHT_JUMPS = {
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
    };

    protected Knight(Color color) {
        super(color);
    }

    @Override
    public char getSymbol() {
        return this.color.isWhite() ? 'C' : 'c';
    }

    @Override
    public boolean isAttacking(Board board, Position from, Position to) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "A posição de destino não pode ser nula.");

        // Cálculo dos deslocamentos
        int deltaX = from.getCol() - to.getCol();
        int deltaY = from.getRow() - to.getRow();

        // Cálculo baseado na distância euclidiana ao quadrado
        int deltaX2 = deltaX * deltaX;
        int deltaY2 = deltaY * deltaY;

        boolean isAttacking = (deltaX2 + deltaY2 == 5);

        return isAttacking;
    }

    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");

        List<Position> moves = new ArrayList<>();

        // Verifica cada possível movimento do cavalo
        for (int[] jump : KNIGHT_JUMPS) {
            // Calcula as posições de destino
            int row = from.getRow() + jump[0];
            int col = from.getCol() + jump[1];

            Position to = Position.at(row, col);

            // Verifica se saiu do tabuleiro
            if (to == null) continue;

            // Verifica se a posição de destino contém uma peça adversária
            if (!board.hasPieceAt(to, this.color)) {
                moves.add(to);
            }
        }
        
        return moves;
    }
    
}
