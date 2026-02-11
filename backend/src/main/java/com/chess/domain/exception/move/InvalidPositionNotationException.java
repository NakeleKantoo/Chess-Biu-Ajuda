package com.chess.domain.exception.move;

import lombok.Getter;

@Getter
public class InvalidPositionNotationException extends RuntimeException {
    
    private final String notation;

    public InvalidPositionNotationException(String notation) {
        super("Notação de posição inválida: " + notation + ". A notação deve estar no formato 'a1', 'e4', etc.");
        this.notation = notation;
    }

}
