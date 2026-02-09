package com.chess.domain.model.game;

import com.chess.domain.model.board.BoardState;

public enum GameEndReason {
    
    CHECKMATE("Xeque-mate"),
    RESIGNATION("Desistência"),

    TIMEOUT("Tempo esgotado"),
    STALEMATE("Empate por afogamento"),
    AGREED_DRAW("Empate por acordo"),
    THREEFOLD_REPETITION("Empate por repetição tripla"),
    FIFTY_MOVE_RULE("Empate pela regra dos cinquenta lances"),
    INSUFFICIENT_MATERIAL("Material insuficiente"),

    ABORTION("Jogo abortado");

    private final String description;

    GameEndReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static GameEndReason fromBoardState(BoardState state) {
        if (state == null) return null;

        return switch (state) {
            case CHECKMATE -> CHECKMATE; // Mapeamento direto
            case STALEMATE -> STALEMATE;
            case DRAW_BY_INSUFFICIENT_MATERIAL -> INSUFFICIENT_MATERIAL;
            case DRAW_BY_FIFTY_MOVE_RULE -> FIFTY_MOVE_RULE;
            case DRAW_BY_THREEFOLD_REPETITION -> THREEFOLD_REPETITION;
            default -> null; // IN_PROGRESS e CHECK não encerram o jogo por si sós nesta lógica
        };
    }
}
