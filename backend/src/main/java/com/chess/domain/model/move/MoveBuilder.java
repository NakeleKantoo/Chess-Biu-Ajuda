package com.chess.domain.model.move;

import java.util.Objects;

import com.chess.domain.model.base.Position;
import com.chess.domain.model.piece.King;
import com.chess.domain.model.piece.Pawn;
import com.chess.domain.model.piece.Piece;
import com.chess.domain.utils.NotationUtils;

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

    private String san; // Notação algébrica
    private String uci; // Notação algébrica

    public MoveBuilder(Move move) {
        Objects.requireNonNull(move, "O movimento não pode ser nulo.");

        this.from = move.getFrom();
        this.to = move.getTo();
        this.movedPiece = move.getMovedPiece();
        this.capturedPiece = move.getCapturedPiece();
        this.promotionPiece = move.getPromotionPiece();
        this.rookFrom = move.getRookFrom();
        this.isCastling = move.isCastling();
        this.isEnPassant = move.isEnPassant();
        this.san = move.getSan();
        this.uci = move.getUci();
    }

    public MoveBuilder(Position from, Position to, Piece movedPiece) {
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "A posição de destino não pode ser nula.");
        Objects.requireNonNull(movedPiece, "A peça movida não pode ser nula.");

        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
    }

    /**
     * Define a peça capturada no movimento.
     * 
     * @param capturedPiece a peça capturada.
     * @return o próprio construtor para encadeamento de chamadas.
      */
    public MoveBuilder capturedPiece(Piece capturedPiece) {
        Objects.requireNonNull(capturedPiece, "Peça capturada não pode ser nula.");

        this.capturedPiece = capturedPiece;

        return this;
    }

    /**
     * Define a peça de promoção no movimento.
     * 
     * @param promotionPiece a peça para a qual o peão será promovido.
     * @return o próprio construtor para encadeamento de chamadas.
      */
    public MoveBuilder promotionPiece(Piece promotionPiece) {
        Objects.requireNonNull(promotionPiece, "Peça de promoção não pode ser nula.");

        this.promotionPiece = promotionPiece;

        return this;
    }

    /**
     * Define que o movimento é um roque.
     * 
     * @param rookFrom a posição da torre no roque.
     * @return o próprio construtor para encadeamento de chamadas.
      */
    public MoveBuilder castling(Position rookFrom) {
        Objects.requireNonNull(rookFrom, "Posição da torre no roque não pode ser nula.");

        this.isCastling = true;
        this.rookFrom = rookFrom;

        return this;
    }

    /**
     * Define que o movimento é um en passant.
     * 
     * @return o próprio construtor para encadeamento de chamadas.
      */
    public MoveBuilder enPassant() {
        this.isEnPassant = true;
        return this;
    }

    public MoveBuilder san(String san) {
        Objects.requireNonNull(san, "Notação algébrica (SAN) não pode ser nula.");

        this.san = san;
        return this;
    }

    /**
     * Constrói o objeto Move com as configurações definidas.
     * Valida a consistência interna antes de criar o objeto.
     * 
     * @return o objeto {@code Move} construído.
      */
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

        this.uci = NotationUtils.toUci(this.from, this.to, this.promotionPiece);

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
    public String getSan() { return san; }
    public String getUci() { return uci; }

}
