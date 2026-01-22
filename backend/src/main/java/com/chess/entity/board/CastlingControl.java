package com.chess.entity.board;

import com.chess.entity.base.Color;

public class CastlingControl {
    private final boolean whiteKingMoved;
    private final boolean blackKingMoved;

    private final boolean whiteKingSideRookMoved;
    private final boolean whiteQueenSideRookMoved;

    private final boolean blackKingSideRookMoved;
    private final boolean blackQueenSideRookMoved;

    // Construtor padrão para o início do jogo (todos os direitos intactos)
    public static final CastlingControl INIT = new CastlingControl(false, false, false, false, false, false);

    public CastlingControl(boolean whiteKingMoved, boolean blackKingMoved, boolean whiteKingSideRookMoved, boolean whiteQueenSideRookMoved, boolean blackKingSideRookMoved, boolean blackQueenSideRookMoved) {
        this.whiteKingMoved = whiteKingMoved;
        this.blackKingMoved = blackKingMoved;
        this.whiteKingSideRookMoved = whiteKingSideRookMoved;
        this.whiteQueenSideRookMoved = whiteQueenSideRookMoved;
        this.blackKingSideRookMoved = blackKingSideRookMoved;
        this.blackQueenSideRookMoved = blackQueenSideRookMoved;
    }

    /**
     * Verifica se o jogador da cor especificada pode realizar o roque do lado do rei.
     * 
     * @param color a cor do jogador.
     * @return {@code true} se o jogador pode realizar o roque do lado do rei, {@code false} caso contrário.
      */
    public boolean canCastleKingSide(Color color) {
        if (color.isWhite())
            return !whiteKingMoved && !whiteKingSideRookMoved;
        else 
            return !blackKingMoved && !blackKingSideRookMoved;
    }

    /**
     * Verifica se o jogador da cor especificada pode realizar o roque do lado da dama.
     * 
     * @param color a cor do jogador.
     * @return {@code true} se o jogador pode realizar o roque do lado da dama, {@code false} caso contrário.
      */
    public boolean canCastleQueenSide(Color color) {
        if (color.isWhite())
            return !whiteKingMoved && !whiteQueenSideRookMoved;
        else
            return !blackKingMoved && !blackQueenSideRookMoved;
    }

    /**
     * Registra o movimento do rei, atualizando os direitos de roque.
     * 
     * @param color a cor do jogador que moveu o rei.
     * @return um novo objeto {@link CastlingControl} com os direitos atualizados.
      */
    public CastlingControl kingMoved(Color color) {
        if (color.isWhite()) {
            return new CastlingControl(true, blackKingMoved, whiteKingSideRookMoved, whiteQueenSideRookMoved, blackKingSideRookMoved, blackQueenSideRookMoved);
        } else {
            return new CastlingControl(whiteKingMoved, true, whiteKingSideRookMoved, whiteQueenSideRookMoved, blackKingSideRookMoved, blackQueenSideRookMoved);
        }
    }

    /**
     * Registra o movimento da torre na ala do rei, atualizando os direitos de roque.
     * 
     * @param color a cor do jogador que moveu a torre na ala do rei.
     * @return um novo objeto {@link CastlingControl} com os direitos atualizados.
      */
    public CastlingControl kingSideRookMoved(Color color) {
        if (color.isWhite()) {
            return new CastlingControl(whiteKingMoved, blackKingMoved, true, whiteQueenSideRookMoved, blackKingSideRookMoved, blackQueenSideRookMoved);
        } else {
            return new CastlingControl(whiteKingMoved, blackKingMoved, whiteKingSideRookMoved, whiteQueenSideRookMoved, true, blackQueenSideRookMoved);
        }
    }

    /**
     * Registra o movimento da torre na ala da dama, atualizando os direitos de roque.
     * 
     * @param color a cor do jogador que moveu a torre na ala da dama.
     * @return um novo objeto {@link CastlingControl} com os direitos atualizados.
      */
    public CastlingControl queenSideRookMoved(Color color) {
        if (color.isWhite()) {
            return new CastlingControl(whiteKingMoved, blackKingMoved, whiteKingSideRookMoved, true, blackKingSideRookMoved, blackQueenSideRookMoved);
        } else {
            return new CastlingControl(whiteKingMoved, blackKingMoved, whiteKingSideRookMoved, whiteQueenSideRookMoved, blackKingSideRookMoved, true);
        }
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(whiteKingMoved, blackKingMoved, whiteKingSideRookMoved, whiteQueenSideRookMoved, blackKingSideRookMoved, blackQueenSideRookMoved);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CastlingControl other = (CastlingControl) obj;
        if (whiteKingMoved != other.whiteKingMoved)
            return false;
        if (blackKingMoved != other.blackKingMoved)
            return false;
        if (whiteKingSideRookMoved != other.whiteKingSideRookMoved)
            return false;
        if (whiteQueenSideRookMoved != other.whiteQueenSideRookMoved)
            return false;
        if (blackKingSideRookMoved != other.blackKingSideRookMoved)
            return false;
        if (blackQueenSideRookMoved != other.blackQueenSideRookMoved)
            return false;
        return true;
    }

}
