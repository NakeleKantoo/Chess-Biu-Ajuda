package com.chess.entity.game;

import com.chess.entity.base.Position;
import com.chess.entity.piece.Pawn;
import com.chess.entity.piece.Piece;

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
    private final boolean needColDesambiguation;
    private final boolean needRowDesambiguation;

    // --- Estado do Jogo ---
    private final boolean isCheck;
    private final boolean isCheckmate;
    private final boolean isStalemate;

    public Move(MoveBuilder builder) {
        this.from = builder.getFrom();
        this.to = builder.getTo();
        this.movedPiece = builder.getMovedPiece();
        this.capturedPiece = builder.getCapturedPiece();
        this.promotionPiece = builder.getPromotionPiece();
        this.rookFrom = builder.getRookFrom();
        this.isCastling = builder.isCastling();
        this.isEnPassant = builder.isEnPassant();
        this.needColDesambiguation = builder.getColDesambiguation();
        this.needRowDesambiguation = builder.getRowDesambiguation();
        this.isCheck = builder.isCheck();
        this.isCheckmate = builder.isCheckmate();
        this.isStalemate = builder.isStalemate();
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
    public boolean isCheck() { return isCheck; }
    public boolean isCheckmate() { return isCheckmate; }
    public boolean isStalemate() { return isStalemate; }

    @Override
    public String toString() {
        StringBuilder notation = new StringBuilder();

        // Notação para Roque
        if (isCastling) {

            // Roque longo (lado da Dama)
            if (to.getCol() < from.getCol()) {
                notation.append("O-O-O");
            }
            // Roque curto (lado do Rei)
            else {
                notation.append("O-O");
            }

        } else {

            // Para peões, não se usa a letra da peça, exceto na captura.
            if (!(movedPiece instanceof Pawn)) {
                notation.append(movedPiece.getSymbol());

                if (needColDesambiguation) {
                    notation.append(from.toString().charAt(0));
                }

                if (needRowDesambiguation) {
                    notation.append(from.toString().charAt(1));
                }
            }

            // Se for uma captura com peão, adiciona a coluna de origem.
            if (movedPiece instanceof Pawn && isCapture()) {
                notation.append(from.toString().charAt(0));
            }

            // Adiciona 'x' para captura
            if (isCapture()) {
                notation.append('x');
            }

            // Posição de destino
            notation.append(to.toString());

            // Promoção
            if (isPromotion()) {
                notation.append('=').append(promotionPiece.getSymbol());
            }
        }

        // Indica cheque ou xeque-mate
        if (isCheckmate) {
            notation.append('#');
        } else if (isCheck) {
            notation.append('+');
        }

        return notation.toString();
    }

}
