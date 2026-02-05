package com.chess.entity.game;

public enum GameEndReason {
    
    CHECKMATE("Xeque-mate"),
    RESIGNATION("Desistência"),

    TIMEOUT("Tempo esgotado"),
    STALEMATE("Empate por afogamento"),
    AGREED_DRAW("Empate por acordo"),
    THREEFOLD_REPETITION("Empate por repetição tripla"),
    FIFTY_MOVE_RULE("Empate pela regra dos cinquenta lances"),
    INSUFFICIENT_MATERIAL("Material insuficiente");

    private final String description;

    GameEndReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
