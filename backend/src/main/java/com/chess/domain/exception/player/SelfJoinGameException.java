package com.chess.domain.exception.player;

import java.util.UUID;

import lombok.Getter;

@Getter
public class SelfJoinGameException extends RuntimeException {
    
    private final UUID playerId;

    public SelfJoinGameException(UUID playerId) {
        super("Jogador não pode entrar em um jogo que ele mesmo criou. (ID: " + playerId + ")");
        this.playerId = playerId;
    }

}
