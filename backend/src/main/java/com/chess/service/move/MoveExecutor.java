package com.chess.service.move;

import java.util.Objects;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.board.BoardBuilder;
import com.chess.entity.board.CastlingControl;
import com.chess.entity.move.Move;
import com.chess.entity.piece.Pawn;
import com.chess.entity.piece.Piece;

public class MoveExecutor {

    BoardBuilder builder;

    /**
     * Executa um movimento no tabuleiro fornecido e retorna o novo estado do tabuleiro após o movimento.
     * 
     * @param board O tabuleiro atual.
     * @param move O movimento a ser executado.
     * @return O novo estado do tabuleiro após a execução do movimento.
      */
    public BoardBuilder executeMove(Board board, Move move) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(move, "O movimento não pode ser nulo.");
        
        this.builder = new BoardBuilder(board);

        movePiece(move.getFrom(), move.getTo());

        if (move.isEnPassant()) enPassant(move);
        else if (move.isCastling()) castle(move);
        else if (move.isPromotion()) promote(move);
        
        if (builder.getCurrentPlayer().isBlack()) {
            builder.incrementFullMoveClock();
        }
        
        builder.incrementHalfMoveClock();
        if (move.getMovedPiece() instanceof Pawn || move.isCapture()) {
            builder.resetHalfMoveClock();
        }

        updateEnPassantTarget(move);
        updateCastlingControl(move);
        updateCurrentPlayer();

        return builder;
    }

    private void movePiece(Position from, Position to) {
        Piece piece = builder.getPieceAt(from);
        builder.removePiece(from);
        builder.placePiece(piece, to);
    }

    private void enPassant(Move move) {
        Position capturedPawnPos = Position.at(move.getFrom().getRow(), move.getTo().getCol());
        builder.removePiece(capturedPawnPos);
    }

    private void castle(Move move) {
        Position rookFrom = move.getRookFrom();
        Position rookTo;
        boolean isLongCastling = move.getTo().getCol() < move.getFrom().getCol();

        if (isLongCastling) {
            rookTo = Position.at(move.getTo().getRow(), 3);
        } else {
            rookTo = Position.at(move.getTo().getRow(), 5);
        }

        movePiece(rookFrom, rookTo);
    }

    private void promote(Move move) {
        builder.removePiece(move.getTo());
        builder.placePiece(move.getPromotionPiece(), move.getTo());
    }

    private void updateCurrentPlayer() {
        Color nextPlayer = builder.getCurrentPlayer().opposite();
        builder.setCurrentPlayer(nextPlayer);
    }

    private void updateEnPassantTarget(Move move) {
        Piece movedPiece = move.getMovedPiece();
        if (!move.getFrom().isNear(move.getTo()) && movedPiece instanceof Pawn) {
            int direction = movedPiece.getColor().isWhite() ? -1 : 1;
            Position enPassantPos = Position.at(move.getFrom().getRow() + direction, move.getFrom().getCol());
            builder.setEnPassantTarget(enPassantPos);
        } else {
            builder.setEnPassantTarget(null);
        }
    }

    private void updateCastlingControl(Move move) {
        CastlingControl builderCastling = builder.getCastlingControl();
        builder.setCastlingControl(builderCastling.update(move));
    }

}
