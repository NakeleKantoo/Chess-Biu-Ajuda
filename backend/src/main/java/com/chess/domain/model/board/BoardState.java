package com.chess.domain.model.board;

public enum BoardState {
    
    IN_PROGRESS("Em progresso"),
    CHECK("Xeque"),
    
    CHECKMATE("Xeque-mate"),
    STALEMATE("Empate por afogamento"),

    DRAW_BY_INSUFFICIENT_MATERIAL("Empate por material insuficiente"),
    DRAW_BY_FIFTY_MOVE_RULE("Empate pela regra dos cinquenta lances"),
    DRAW_BY_THREEFOLD_REPETITION("Empate por tripla repetição");

    private final String description;

    BoardState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Verifica se o jogo está em andamento.
     * 
     * @return {@code true} se o jogo estiver em andamento, {@code false} caso contrário.
      */
    public boolean isInProgress() {
        return this == IN_PROGRESS || this == CHECK;
    }

    /**
     * Verifica se o jogo terminou.
     * 
     * @return {@code true} se o jogo terminou, {@code false} caso contrário.
     */
    public boolean isGameOver() {
        return this != IN_PROGRESS && this != CHECK;
    }

    /**
     * Verifica se o resultado é algum tipo de empate.
     * 
     * @return {@code true} se o resultado for empate, {@code false} caso contrário.
     */
    public boolean isDraw() {
        return  this == STALEMATE || 
                this == DRAW_BY_INSUFFICIENT_MATERIAL || 
                this == DRAW_BY_FIFTY_MOVE_RULE || 
                this == DRAW_BY_THREEFOLD_REPETITION;
    }

    public boolean isCheck() {
        return this == CHECK;
    }

    public boolean isCheckmate() {
        return this == CHECKMATE;
    }

}