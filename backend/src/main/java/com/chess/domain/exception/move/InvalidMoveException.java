package com.chess.domain.exception.move;

import com.chess.domain.model.base.Position;

import lombok.Getter;

@Getter
public class InvalidMoveException extends RuntimeException {

    private final Position from;
    private final Position to;

    public InvalidMoveException(Position from, Position to) {
        super(String.format("Movimento inválido de '%s' para '%s'", from, to));
        this.from = from;
        this.to = to;
    }

    public InvalidMoveException(Position from) {
        super(String.format("Ausência de peça na posição de origem '%s'", from));
        this.from = from;
        this.to = null;
    }
    
}
