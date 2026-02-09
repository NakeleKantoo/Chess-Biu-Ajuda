package com.chess.domain.model.game;

import com.chess.domain.model.base.Color;
import com.chess.domain.model.board.BoardState;

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
