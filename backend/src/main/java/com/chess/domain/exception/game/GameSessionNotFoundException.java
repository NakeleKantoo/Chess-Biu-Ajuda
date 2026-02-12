package com.chess.domain.exception.game;

import java.util.UUID;

import lombok.Getter;

@Getter
public class GameSessionNotFoundException extends RuntimeException {
    
    private final UUID gameId;

    public GameSessionNotFoundException(UUID gameId) {
        super("Sessão de jogo não encontrada para o ID: " + gameId +
            ". Verifique se a sessão está ativa ou se o jogo foi finalizado.");
        this.gameId = gameId;
    }

}
