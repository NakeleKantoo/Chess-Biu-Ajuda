package com.chess.domain.exception.game;

import java.util.UUID;

import lombok.Getter;

@Getter
public class GameNotFoundException extends RuntimeException {

    private final UUID gameId;
    
    public GameNotFoundException(UUID gameId) {
        super("Jogo não encontrado para o ID: " + gameId +
            ". Verifique se o ID está correto e se o jogo foi finalizado.");
        this.gameId = gameId;
    }
    
}
