package com.chess.domain.exception.move;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;
import com.chess.domain.model.base.Position;

import lombok.Getter;

@Getter
public class InvalidMoveException extends ChessException {

    private final Position from;
    private final Position to;

    private final String type;
    private final Map<String, Object> details;

    public InvalidMoveException(Position from, Position to) {
        super(String.format("Movimento inválido de '%s' para '%s'", from, to));
        this.from = from;
        this.to = to;
        this.type = "Invalid Move";
        this.details = Map.of("from", from.toString(), "to", to.toString());
    }

    public InvalidMoveException(Position from) {
        super(String.format("Ausência de peça na posição de origem '%s'", from));
        this.from = from;
        this.to = null;
        this.type = "Invalid Move - No Piece at Origin";
        this.details = Map.of("from", from.toString());
    }

    @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }
    
}
