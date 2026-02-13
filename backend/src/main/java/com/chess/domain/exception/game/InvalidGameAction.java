package com.chess.domain.exception.game;

import lombok.Getter;

@Getter
public class InvalidGameAction extends RuntimeException {
    
    private final String action;

    public InvalidGameAction(String action) {
        super("Ação inválida para jogo: " + action);
        this.action = action;
    }

}
