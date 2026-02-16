package com.chess.domain.exception.move;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;
import com.chess.domain.model.piece.Piece;

import lombok.Getter;

@Getter
public class InvalidPromotionPieceException extends ChessException {

    private final Piece piece;

    public InvalidPromotionPieceException(Piece piece) {
        super("Peça de promoção inválida: " + piece.getName() +
            ". A peça deve ser uma Rainha, Torre, Bispo ou Cavalo.");
        this.piece = piece;
    }

    @Override public String getType() { return "Invalid Promotion Piece"; }
    @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("piece", piece.getName());
    }
    
}
