package com.chess.domain.model.move;

import java.util.Objects;

import com.chess.domain.model.base.Position;
import com.chess.domain.model.piece.Piece;

public class Move {
    
    // --- Campos Essenciais ---
    private final Position from;
    private final Position to;
    private final Piece movedPiece;

    private final Piece capturedPiece; // Pode ser nulo
    private final Piece promotionPiece; // Pode ser nulo
    private final Position rookFrom; // Pode ser nulo

    // --- Flags para Movimentos Especiais ---
    private final boolean isCastling;
    private final boolean isEnPassant;

    private final String san; // Notação algébrica
    private final String uci; // Notação algébrica

    public Move(MoveBuilder builder) {
        this.from = builder.getFrom();
        this.to = builder.getTo();
        this.movedPiece = builder.getMovedPiece();
        this.capturedPiece = builder.getCapturedPiece();
        this.promotionPiece = builder.getPromotionPiece();
        this.rookFrom = builder.getRookFrom();
        this.isCastling = builder.isCastling();
        this.isEnPassant = builder.isEnPassant();
        this.san = builder.getSan();
        this.uci = builder.getUci();
    }

    // --- Getters ---
    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public Piece getMovedPiece() { return movedPiece; }
    public Piece getCapturedPiece() { return capturedPiece; }
    public Piece getPromotionPiece() { return promotionPiece; }
    public Position getRookFrom() { return rookFrom; }
    public boolean isCastling() { return isCastling; }
    public boolean isEnPassant() { return isEnPassant; }
    public boolean isCapture() { return capturedPiece != null; }
    public boolean isPromotion() { return promotionPiece != null; }
    public String getSan() { return san; }
    public String getUci() { return uci; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return  Objects.equals(from, move.from) &&
                Objects.equals(to, move.to) &&
                Objects.equals(promotionPiece, move.promotionPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, promotionPiece);
    }

}
