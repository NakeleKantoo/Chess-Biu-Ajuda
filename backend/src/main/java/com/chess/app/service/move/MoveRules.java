package com.chess.app.service.move;

import com.chess.domain.model.base.Direction;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.board.BaseBoard;
import com.chess.domain.model.board.CastlingControl;
import com.chess.domain.model.move.MoveBuilder;
import com.chess.domain.model.piece.Bishop;
import com.chess.domain.model.piece.King;
import com.chess.domain.model.piece.Knight;
import com.chess.domain.model.piece.Pawn;
import com.chess.domain.model.piece.Piece;
import com.chess.domain.model.piece.Queen;
import com.chess.domain.model.piece.Rook;

public class MoveRules {

    private MoveRules() {}

    public static void applyCaptureRule(BaseBoard board, Position to, MoveBuilder moveBuilder) {
        Piece capturedPiece = board.getPieceAt(to);
        if (capturedPiece != null) {
            moveBuilder.capturedPiece(capturedPiece);
        }
    }

    public static void applyCastleRule(BaseBoard board, Position from, Position to, Piece piece, MoveBuilder moveBuilder) {
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

    public static void applyEnPassantRule(BaseBoard board, Position from, Position to, Piece piece, MoveBuilder moveBuilder) {
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

        return  piece instanceof Queen ||
                piece instanceof Rook ||
                piece instanceof Bishop ||
                piece instanceof Knight;
    }

}
