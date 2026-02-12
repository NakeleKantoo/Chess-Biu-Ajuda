package com.chess.domain.exception.game;

import lombok.Getter;

@Getter
public class PendingGameNotFoundException extends RuntimeException {

    private final String gameCode;
    
    public PendingGameNotFoundException(String gameCode) {
        super("Nenhum jogo pendente encontrado para o código: " + gameCode +
            ". Verifique se o código está correto e se o jogo foi criado.");
        this.gameCode = gameCode;
    }

}
