package com.chess.app.service.move;

import java.util.Objects;

import com.chess.domain.model.base.Color;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.board.Board;
import com.chess.domain.model.board.BoardBuilder;
import com.chess.domain.model.board.CastlingControl;
import com.chess.domain.model.move.Move;
import com.chess.domain.model.piece.Pawn;
import com.chess.domain.model.piece.Piece;

public class MoveExecutor {

    private MoveExecutor() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    /**
     * Executa um movimento no tabuleiro fornecido e retorna o novo estado do tabuleiro após o movimento.
     * 
     * @param board O tabuleiro atual.
     * @param move O movimento a ser executado.
     * @return O novo estado do tabuleiro após a execução do movimento.
      */
    public static BoardBuilder executeMove(Board board, Move move) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(move, "O movimento não pode ser nulo.");

        BoardBuilder builder = new BoardBuilder(board);

        movePiece(builder, move.getFrom(), move.getTo());

        if (move.isEnPassant()) enPassant(builder, move);
        else if (move.isCastling()) castle(builder, move);
        else if (move.isPromotion()) promote(builder, move);
        
        if (builder.getCurrentPlayer().isBlack()) {
            builder.incrementFullMoveClock();
        }
        
        builder.incrementHalfMoveClock();
        if (move.getMovedPiece() instanceof Pawn || move.isCapture()) {
            builder.resetHalfMoveClock();
        }

        updateEnPassantTarget(builder, move);
        updateCastlingControl(builder, move);
        updateCurrentPlayer(builder);

        return builder;
    }

    private static void movePiece(BoardBuilder builder, Position from, Position to) {
        Piece piece = builder.getPieceAt(from);
        builder.removePiece(from);
        builder.placePiece(piece, to);
    }

    private static void enPassant(BoardBuilder builder, Move move) {
        Position capturedPawnPos = Position.at(move.getFrom().getRow(), move.getTo().getCol());
        builder.removePiece(capturedPawnPos);
    }

    private static void castle(BoardBuilder builder, Move move) {
        Position rookFrom = move.getRookFrom();
        Position rookTo;
        boolean isLongCastling = move.getTo().getCol() < move.getFrom().getCol();

        if (isLongCastling) {
            rookTo = Position.at(move.getTo().getRow(), 3);
        } else {
            rookTo = Position.at(move.getTo().getRow(), 5);
        }

        movePiece(builder, rookFrom, rookTo);
    }

    private static void promote(BoardBuilder builder, Move move) {
        builder.removePiece(move.getTo());
        builder.placePiece(move.getPromotionPiece(), move.getTo());
    }

    private static void updateCurrentPlayer(BoardBuilder builder) {
        Color nextPlayer = builder.getCurrentPlayer().opposite();
        builder.setCurrentPlayer(nextPlayer);
    }

    private static void updateEnPassantTarget(BoardBuilder builder, Move move) {
        Piece movedPiece = move.getMovedPiece();
        if (!move.getFrom().isNear(move.getTo()) && movedPiece instanceof Pawn) {
            int direction = movedPiece.getColor().isWhite() ? -1 : 1;
            Position enPassantPos = Position.at(move.getFrom().getRow() + direction, move.getFrom().getCol());
            builder.setEnPassantTarget(enPassantPos);
        } else {
            builder.setEnPassantTarget(null);
        }
    }

    private static void updateCastlingControl(BoardBuilder builder, Move move) {
        CastlingControl builderCastling = builder.getCastlingControl();
        builder.setCastlingControl(builderCastling.update(move));
    }

}
