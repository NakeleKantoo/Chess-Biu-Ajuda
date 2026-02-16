package com.chess.domain.exception.game;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class PendingGameNotFoundException extends ChessException {

    private final String gameCode;
    
    public PendingGameNotFoundException(String gameCode) {
        super("Nenhum jogo pendente encontrado para o código: " + gameCode +
            ". Verifique se o código está correto e se o jogo foi criado.");
        this.gameCode = gameCode;
    }

    @Override public String getType() { return "Pending Game Not Found"; }
    @Override public HttpStatus getStatus() { return HttpStatus.NOT_FOUND; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("gameId", gameCode);
    }

}
