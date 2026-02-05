package com.chess.entity.game;

public enum GameState {
    ACTIVE("Ativo"),
    WHITE_WON("Brancas venceram"),
    BLACK_WON("Pretas venceram"),
    DRAW("Empate"),
    ABORTED("Abortado");

    private final String description;

    GameState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
