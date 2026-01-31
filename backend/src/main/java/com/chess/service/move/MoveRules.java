package com.chess.service.move;

import com.chess.entity.base.Direction;
import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.board.CastlingControl;
import com.chess.entity.move.MoveBuilder;
import com.chess.entity.piece.King;
import com.chess.entity.piece.Pawn;
import com.chess.entity.piece.Piece;

public class MoveRules {

    private MoveRules() {}

    public static void applyCaptureRule(Board board, Position to, MoveBuilder moveBuilder) {
        Piece capturedPiece = board.getPieceAt(to);
        if (capturedPiece != null) {
            moveBuilder.capturedPiece(capturedPiece);
        }
    }

    public static void applyCastleRule(Board board, Position from, Position to, Piece piece, MoveBuilder moveBuilder) {
        boolean isCastling = piece instanceof King && !from.isNear(to); 
        if (!isCastling) {
            return;
        }

        boolean isKingSide = to.getCol() > from.getCol();
        boolean isWhite = board.getCurrentPlayer().isWhite();
        CastlingControl castlingControl = board.getCastlingControl();
        Position rookFrom;

        if (isWhite) {
            if (isKingSide) {
                rookFrom = Position.at(7, castlingControl.getWhiteKingSideRookCol());
            } else {
                rookFrom = Position.at(7, castlingControl.getWhiteQueenSideRookCol());
            }
        } else {
            if (isKingSide) {
                rookFrom = Position.at(0, castlingControl.getBlackKingSideRookCol());
            } else {
                rookFrom = Position.at(0, castlingControl.getBlackQueenSideRookCol());
            }
        }

        moveBuilder.castling(rookFrom);
    }

    public static void applyEnPassantRule(Board board, Position from, Position to, Piece piece, MoveBuilder moveBuilder) {
        boolean isPawn = piece instanceof Pawn;
        if (!isPawn) {
            return;
        }

        Direction dir = Direction.get(from, to);
        if (dir.isDiagonal() && !board.hasPieceAt(to)) {
            Piece capturedPiece = board.getPieceAt(Position.at(from.getRow(), to.getCol()));
            moveBuilder.capturedPiece(capturedPiece);
            moveBuilder.enPassant();
        }
    }

    public static boolean isPromotion(Position to, Piece piece) {
        if (!(piece instanceof Pawn)) {
            return false;
        }

        int promotionRow = piece.getColor().isWhite() ? 0 : 7;
        return to.getRow() == promotionRow;
    }

    public static boolean isPromotionPiece(Piece piece) {
        if (piece == null) {
            return false;
        }

        char type = Character.toUpperCase(piece.getSymbol());
        return type == 'D' || type == 'T' || type == 'B' || type == 'C';
    }
}
