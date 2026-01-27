package com.chess.service;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.board.BoardBuilder;
import com.chess.entity.board.BoardState;
import com.chess.entity.board.CastlingControl;
import com.chess.entity.game.Move;
import com.chess.entity.piece.Pawn;
import com.chess.entity.piece.Piece;

public class MoveExecutor {

    BoardBuilder builder;

    public Board executeMove(Board board, Move move) {
        this.builder = new BoardBuilder(board);

        movePiece(move.getFrom(), move.getTo());

        if (move.isEnPassant()) enPassant(move);
        else if (move.isCastling()) castle(move);
        else if (move.isPromotion()) promote(move);
        
        if (move.isCheckmate()) builder.setBoardState(BoardState.CHECKMATE);
        else if (move.isCheck()) builder.setBoardState(BoardState.CHECK);
        else builder.setBoardState(BoardState.IN_PROGRESS);

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

        return builder.build();
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
