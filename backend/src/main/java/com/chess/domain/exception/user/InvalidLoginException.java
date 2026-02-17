package com.chess.domain.exception.user;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

public class InvalidLoginException extends ChessException {

    public InvalidLoginException() {
        super("Usuário ou senha inválidos.");
    }

    @Override public String getType() { return "Invalid Login"; }
    @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of();
    }
}
