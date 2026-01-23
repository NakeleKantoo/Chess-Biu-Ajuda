package com.chess.entity.game;

import com.chess.entity.base.Position;
import com.chess.entity.piece.King;
import com.chess.entity.piece.Pawn;
import com.chess.entity.piece.Piece;

public class MoveBuilder {

    // --- Campos Essenciais ---
    private Position from;
    private Position to;
    private Piece movedPiece;

    private Piece capturedPiece; // Pode ser nulo
    private Piece promotionPiece; // Pode ser nulo
    private Position rookFrom; // Pode ser nulo

    // --- Flags para Movimentos Especiais ---
    private boolean isCastling;
    private boolean isEnPassant;
    private boolean needColDesambiguation;
    private boolean needRowDesambiguation;

    // --- Estado do Jogo ---
    private boolean isCheck;
    private boolean isCheckmate;
    private boolean isStalemate;

    public MoveBuilder(Position from, Position to, Piece movedPiece) {
        if (from == null || to == null || movedPiece == null) {
            throw new IllegalArgumentException("Posições e peça movida não podem ser nulas.");
        }
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
    }

    public MoveBuilder capturedPiece(Piece capturedPiece) {
        if (capturedPiece == null) {
            throw new IllegalArgumentException("Peça capturada não pode ser nula.");
        }
        this.capturedPiece = capturedPiece;
        return this;
    }

    public MoveBuilder promotionPiece(Piece promotionPiece) {
        if (promotionPiece == null) {
            throw new IllegalArgumentException("Peça de promoção não pode ser nula.");
        }
        this.promotionPiece = promotionPiece;
        return this;
    }

    public MoveBuilder castling(Position rookFrom) {
        if (rookFrom == null) {
            throw new IllegalArgumentException("Posições do roque não podem ser nulas.");
        }
        this.isCastling = true;
        this.rookFrom = rookFrom;
        return this;
    }

    public MoveBuilder enPassant() {
        this.isEnPassant = true;
        return this;
    }

    public MoveBuilder needColDesambiguation() {
        this.needColDesambiguation = true;
        return this;
    }

    public MoveBuilder needRowDesambiguation() {
        this.needRowDesambiguation = true;
        return this;
    }

    public MoveBuilder check() {
        this.isCheck = true;
        return this;
    }

    public MoveBuilder checkmate() {
        this.isCheckmate = true;
        this.isCheck = true; // Xeque-mate implica xeque
        return this;
    }

    public MoveBuilder stalemate() {
        this.isStalemate = true;
        return this;
    }

    public Move build() {
        // Validações de consistência interna
        if (from.equals(to)) {
            throw new IllegalStateException("A posição de origem não pode ser igual à de destino.");
        }
        if (isCastling) {
            if (isEnPassant || promotionPiece != null || capturedPiece != null) {
                throw new IllegalStateException("Um movimento de roque não pode ser combinado com outras ações.");
            }
            if (!(movedPiece instanceof King)) {
                throw new IllegalStateException("A peça movida em um roque deve ser o Rei.");
            }
            if (rookFrom == null) {
                throw new IllegalStateException("A posição de origem da torre deve ser fornecida para o roque.");
            }
        }
        if (promotionPiece != null && !(movedPiece instanceof Pawn)) {
            throw new IllegalStateException("Apenas peões podem ser promovidos.");
        }
        if (isEnPassant) {
            if (!(movedPiece instanceof Pawn)) {
                throw new IllegalStateException("Apenas peões podem realizar en passant.");
            }
            if (!(capturedPiece instanceof Pawn)) {
                throw new IllegalStateException("En passant deve capturar um peão.");
            }
        }
        if (isCheckmate && isStalemate) {
            throw new IllegalStateException("Um movimento não pode resultar em xeque-mate e empate ao mesmo tempo.");
        }

        return new Move(this);
    }

    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public Piece getMovedPiece() { return movedPiece; }
    public Piece getCapturedPiece() { return capturedPiece; }
    public Piece getPromotionPiece() { return promotionPiece; }
    public Position getRookFrom() { return rookFrom; }
    public boolean isCastling() { return isCastling; }
    public boolean isEnPassant() { return isEnPassant; }
    public boolean getColDesambiguation() { return needColDesambiguation; }
    public boolean getRowDesambiguation() { return needRowDesambiguation; }
    public boolean isCheck() { return isCheck; }
    public boolean isCheckmate() { return isCheckmate; }
    public boolean isStalemate() { return isStalemate; }

}
