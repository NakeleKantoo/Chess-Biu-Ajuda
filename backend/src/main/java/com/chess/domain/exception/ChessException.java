package com.chess.domain.exception;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;

public abstract class ChessException extends RuntimeException {

    public ChessException(String message) {
        super(message);
    }

    public abstract String getType();
    public abstract HttpStatus getStatus();

    public Map<String, Object> getDetails() {
        return Collections.emptyMap();
    }

}
