package com.chess.entity.board;

import java.util.List;

import com.chess.entity.base.Color;
import com.chess.entity.base.Direction;
import com.chess.entity.base.Position;
import com.chess.entity.piece.Piece;

public class BoardAnalyzer {
    
    /**
     * Verifica se uma posição específica está sob ataque por qualquer peça da cor atacante.
     * Útil para detecção de Xeque e validação de casas de passagem no Roque.
     * 
     * @param board o tabuleiro atual.
     * @param target A posição alvo que se deseja verificar.
     * @param attackerColor A cor das peças que podem estar atacando.
     * @return {@code true} se pelo menos uma peça da cor atacante ameaça a posição alvo.
     */
    public static boolean isUnderAttack(Board board, Position target, Color attackerColor) {
        // Acessa diretamente o mapa para evitar o overhead de clonagem do getter público
        List<Position> attackerPositions = board.piecesPositionsByColor.get(attackerColor);
        
        if (attackerPositions == null || attackerPositions.isEmpty()) {
            return false;
        }

        for (Position attackerPos : attackerPositions) {
            Piece attacker = board.getPieceAt(attackerPos);
            
            // Verifica se a peça ataca o alvo (isAttacking já valida a geometria e bloqueios)
            if (attacker != null && attacker.isAttacking(board, attackerPos, target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se o caminho entre duas posições está sob ataque por qualquer peça da cor atacante.
     * Inclui a posição inicial.
     * 
     * @param board o tabuleiro atual.
     * @param from a posição inicial.
     * @param to a posição final.
     * @param attackerColor a cor das peças que podem estar atacando.
     * @return {@code true} se pelo menos uma peça da cor atacante ameaça alguma posição no caminho.
      */
    public static boolean isPathUnderAttack(Board board, Position from, Position to, Color attackerColor) {
        Direction dir = Direction.get(from, to);
        if (dir == null) {
            return false;
        }

        Position current = from;
        while (true) {
            if (current.equals(to)) {
                break;
            }
            if (isUnderAttack(board, current, attackerColor)) {
                return true;
            }
            current = current.getNext(dir);
        }

        return false;
    }

    /**
     * Verifica se o caminho entre duas posições está livre de peças.
     * Não considera a posição inicial e final.
     * 
     * @param board o tabuleiro atual.
     * @param from a posição inicial.
     * @param to a posição final.
     * @return {@code true} se o caminho estiver livre, {@code false} caso contrário.
      */
    public static boolean isPathClear(Board board, Position from, Position to) {
        Direction dir = Direction.get(from, to);
        if (dir == null) {
            return false;
        }

        while (true) {
            from = from.getNext(dir);
            if (from.equals(to)) {
                break;
            }
            if (board.hasPieceAt(from)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica se o rei da cor especificada está em xeque.
     * 
     * @param board o tabuleiro atual.
     * @param color A cor do rei a ser verificado.
     * @return {@code true} se o rei da cor especificada estiver em xeque, {@code false} caso contrário.
      */
    public static boolean isInCheck(Board board, Color color) {
        return isUnderAttack(board, board.getKingPosition(color), color.opposite());
    }
}
