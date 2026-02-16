package com.chess.domain.exception.game;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class GameSessionNotFoundException extends ChessException {
    
    private final UUID gameId;

    public GameSessionNotFoundException(UUID gameId) {
        super("Sessão de jogo não encontrada para o ID: " + gameId +
            ". Verifique se a sessão está ativa ou se o jogo foi finalizado.");
        this.gameId = gameId;
    }

    @Override public String getType() { return "Game Session Not Found"; }
    @Override public HttpStatus getStatus() { return HttpStatus.NOT_FOUND; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("gameId", gameId);
    }

}
