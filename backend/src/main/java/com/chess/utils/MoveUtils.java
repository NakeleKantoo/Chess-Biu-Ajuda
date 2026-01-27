package com.chess.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.chess.entity.base.*;
import com.chess.entity.board.Board;

public class MoveUtils {
    
    /**
     * Gera uma lista de movimentos possíveis para uma peça em determinadas direções.
     * 
     * @param board O tabuleiro de xadrez.
     * @param pieceColor A cor da peça que está se movendo.
     * @param from A posição de origem da peça.
     * @param directions As direções nas quais a peça pode se mover.
     * @return Uma lista de posições possíveis para a peça se mover.
      */
    public static List<Position> getSlidingMoves(Board board, Color pieceColor, Position from, Direction[] directions) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(pieceColor, "A cor da peça não pode ser nula.");
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");
        Objects.requireNonNull(directions, "As direções não podem ser nulas.");

        List<Position> possibleMoves = new ArrayList<>();

        // Itera sobre cada direção fornecida
        for (Direction dir : directions) {
            Position to = from;
            while (true) {
                to = to.getNext(dir);

                // Verifica se saiu do tabuleiro
                if (to == null) break;

                // Verifica se o caminho está livre e adiciona a posição
                if (!board.hasPieceAt(to)) {
                    possibleMoves.add(to);
                    continue;
                }

                // Se encontrar uma peça, verifica se é adversária para captura
                if (board.hasPieceAt(to, pieceColor.opposite())) {
                    possibleMoves.add(to);
                }

                // Interrompe a busca nessa direção após encontrar qualquer peça
                break;
            };
        }

        return possibleMoves;
    }

}
