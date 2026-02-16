package com.chess.domain.exception.move;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class InvalidPositionNotationException extends ChessException {
    
    private final String notation;

    public InvalidPositionNotationException(String notation) {
        super("Notação de posição inválida: " + notation + ". A notação deve estar no formato 'a1', 'e4', etc.");
        this.notation = notation;
    }

    @Override public String getType() { return "Invalid Position Notation"; }
    @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("notation", notation);
    }

}
