package com.chess.domain.exception.player;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.chess.domain.exception.ChessException;

import lombok.Getter;

@Getter
public class SelfJoinGameException extends ChessException {
    
    private final UUID playerId;

    public SelfJoinGameException(UUID playerId) {
        super("Jogador não pode entrar em um jogo que ele mesmo criou. (ID: " + playerId + ")");
        this.playerId = playerId;
    }

    @Override public String getType() { return "Self Join Game"; }
    @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("playerId", playerId);
    }

}
