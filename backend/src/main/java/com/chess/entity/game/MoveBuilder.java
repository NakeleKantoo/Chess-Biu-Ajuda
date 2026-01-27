package com.chess.entity.game;

import java.util.Objects;

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

    /**
     * Define que o movimento necessita de desambiguação pela coluna.
     * 
     * @return o próprio construtor para encadeamento de chamadas.
     */
    public MoveBuilder needColDesambiguation() {
        this.needColDesambiguation = true;
        return this;
    }

    /**
     * Define que o movimento necessita de desambiguação pela linha.
     * 
     * @return o próprio construtor para encadeamento de chamadas.
      */
    public MoveBuilder needRowDesambiguation() {
        this.needRowDesambiguation = true;
        return this;
    }

    /**
     * Define que o movimento resulta em xeque.
     * 
     * @return o próprio construtor para encadeamento de chamadas.
     */
    public MoveBuilder check() {
        this.isCheck = true;
        return this;
    }

    /**
     * Define que o movimento resulta em xeque-mate.
     * 
     * @return o próprio construtor para encadeamento de chamadas.
      */
    public MoveBuilder checkmate() {
        this.isCheckmate = true;
        this.isCheck = true; // Xeque-mate implica xeque
        return this;
    }

    /**
     * Define que o movimento resulta em empate.
     * 
     * @return o próprio construtor para encadeamento de chamadas.
      */
    public MoveBuilder stalemate() {
        this.isStalemate = true;
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
