package com.chess.utils;

import java.util.List;

import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.board.BoardState;
import com.chess.entity.move.Move;
import com.chess.entity.piece.Pawn;
import com.chess.entity.piece.Piece;

public class NotationUtils {

    public static String toUci(Position from, Position to, Piece promotionPiece) {
        StringBuilder uci = new StringBuilder();
        
        uci.append(from.toString());
        uci.append(to.toString());

        if (promotionPiece != null) {
            uci.append(Character.toLowerCase(promotionPiece.getSymbol()));
        }

        return uci.toString();
    }
    
    public static String toSan(Move move, Board boardBeforeMove, BoardState boardStateAfterMove, List<Move> legalMoves) {
        StringBuilder san = new StringBuilder();

        if (move.isCastling()) {
            // O-O ou O-O-O
            appendCastle(san, move);
        } else {
            boolean isPawn = move.getMovedPiece() instanceof Pawn;

            if (!isPawn) {
                // Adiciona a letra da peça (N, B, R, Q, K)
                appendPieceSymbol(san, move);

                // Adiciona desambiguação se necessário (ex: Nbd2 ou N1f3)
                appendDesambiguation(san, move, legalMoves);
            } else if (move.isCapture()) {
                // Para peões, em caso de captura, adiciona a coluna de origem
                san.append(move.getFrom().toString().charAt(0));
            }

            // Adiciona 'x' para captura
            appendCapture(move, san);
            // Adiciona a casa de destino
            appendTo(move, san);
            // Adiciona promoção se houver
            appendPromotion(move, san);
        }

        // Adiciona '+' para xeque ou '#' para xeque-mate
        appendChecks(san, boardStateAfterMove);

        return san.toString();
    }

    private static void appendCastle(StringBuilder san, Move move) {
        boolean isKingside = move.getTo().getCol() > move.getFrom().getCol();

        if (isKingside) {
            san.append("O-O");
        } else {
            san.append("O-O-O");
        }
    }

    private static void appendPieceSymbol(StringBuilder san, Move move) {
        char symbol = Character.toUpperCase(move.getMovedPiece().getSymbol());
        san.append(symbol);
    }

    private static void appendDesambiguation(StringBuilder san, Move move, List<Move> legalMoves) {
        List<Move> similarMoves = legalMoves.stream()
            .filter(m -> m.getMovedPiece().equals(move.getMovedPiece()) &&
                        m.getTo().equals(move.getTo()) &&
                        !m.getFrom().equals(move.getFrom()))
            .toList();

        if (similarMoves.isEmpty()) {
            return; // Sem ambiguidade
        }

        boolean sameFile = similarMoves.stream().anyMatch(m -> m.getFrom().getCol() == move.getFrom().getCol());
        boolean sameRank = similarMoves.stream().anyMatch(m -> m.getFrom().getRow() == move.getFrom().getRow());

        if (!sameFile) {
            san.append(move.getFrom().toString().charAt(0)); // Coluna
        } else if (!sameRank) {
            san.append(move.getFrom().toString().charAt(1)); // Linha
        } else {
            san.append(move.getFrom().toString()); // Coluna + Linha
        }
    }

    private static void appendCapture(Move move, StringBuilder san) {
        // Adiciona 'x' para captura
        if (move.isCapture()) {
            san.append('x');
        }
    }

    private static void appendTo(Move move, StringBuilder san) {
        san.append(move.getTo().toString());
    }

    private static void appendPromotion(Move move, StringBuilder san) {
        if (move.isPromotion()) {
            san.append('=').append(move.getPromotionPiece().getSymbol());
        }
    }

    private static void appendChecks(StringBuilder san, BoardState boardStateAfterMove) {
        if (boardStateAfterMove.isCheckmate()) {
            san.append("#");
        } else if (boardStateAfterMove.isCheck()) {
            san.append("+");
        }
    }
    
}
