package com.chess.entity.game;

import com.chess.entity.base.Color;
import com.chess.entity.board.BoardState;

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

    public static GameState fromBoardState(BoardState state, Color currentPlayer) {
        if (state == null) return null;

        if (state.isInProgress()) {
            return ACTIVE;
        } else if (state == BoardState.CHECKMATE) {
            return currentPlayer == Color.WHITE ? BLACK_WON : WHITE_WON; // O jogador atual é o perdedor
        } else {
            return DRAW;
        } 
    }
}
