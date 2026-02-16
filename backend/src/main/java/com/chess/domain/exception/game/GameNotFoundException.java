package com.chess.domain.exception.game;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class GameNotFoundException extends ChessException {

    private final UUID gameId;
    
    public GameNotFoundException(UUID gameId) {
        super("Jogo não encontrado para o ID: " + gameId +
            ". Verifique se o ID está correto e se o jogo foi finalizado.");
        this.gameId = gameId;
    }

    @Override public String getType() { return "Game Not Found"; }
    @Override public HttpStatus getStatus() { return HttpStatus.NOT_FOUND; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("gameId", gameId);
    }
    
}
