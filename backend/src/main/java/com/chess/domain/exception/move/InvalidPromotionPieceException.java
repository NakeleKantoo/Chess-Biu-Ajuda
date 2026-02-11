package com.chess.domain.exception.move;

import com.chess.domain.model.piece.Piece;

import lombok.Getter;

@Getter
public class InvalidPromotionPieceException extends RuntimeException {

    private final Piece piece;

    public InvalidPromotionPieceException(Piece piece) {
        super("Peça de promoção inválida: " + piece.getName() +
            ". A peça deve ser uma Rainha, Torre, Bispo ou Cavalo.");
        this.piece = piece;
    }
    
}
