package com.chess.entity.piece;

import java.util.List;
import java.util.Objects;

import com.chess.entity.base.*;
import com.chess.entity.board.BaseBoard;

public abstract class Piece {

    protected Color color;

    protected Piece(Color color) {
        this.color = color;
    }

    /**
     * Cria uma peça de xadrez com base no símbolo fornecido.
     * <p>
     * Símbolos maiúsculos representam peças brancas, enquanto minúsculos representam peças pretas.
     * <ul>
     * <li>'P'/'p' - Peão</li>
     * <li>'T'/'t' - Torre</li>
     * <li>'C'/'c' - Cavalo</li>
     * <li>'B'/'b' - Bispo</li>
     * <li>'D'/'d' - Dama</li>
     * <li>'R'/'r' - Rei</li>
     * </ul>
     * 
     * @param symbol O símbolo representando a peça.
     * @return A peça correspondente ao símbolo.
     * @throws IllegalArgumentException Se o símbolo for inválido.
      */
    public static Piece create(char symbol) {
        Objects.requireNonNull(symbol, "O símbolo da peça não pode ser nulo.");

        Color color = Character.isUpperCase(symbol) ? Color.WHITE : Color.BLACK;
        switch (Character.toUpperCase(symbol)) {
            case 'P':
                return new Pawn(color);
            case 'T':
                return new Rook(color);
            case 'C':
                return new Knight(color);
            case 'B':
                return new Bishop(color);
            case 'D':
                return new Queen(color);
            case 'R':
                return new King(color);
            default:
                throw new IllegalArgumentException("Símbolo de peça inválido: " + symbol);
        }
    }
    
    public Color getColor() {
        return color;
    }

    public abstract char getSymbol();

    /**
     * Verifica se esta peça, a partir de uma posição 'from', ataca uma casa 'to'.
     * <p>
     * Este método avalia apenas a capacidade de ataque da peça com base em sua
     * geometria de movimento, ignorando se a casa de destino está ocupada.
     * <p>
     * Útil para verificar se o rei está em xeque ou para lógicas de movimento especiais.
     *
     * @param board O tabuleiro de xadrez atual.
     * @param from A posição de origem da peça atacante.
     * @param to   A posição da casa alvo que pode estar sob ataque.
     * @return {@code true} se a peça em 'from' tem a casa 'to' em seu padrão de ataque,
     *         {@code false} caso contrário.
     */
    public abstract boolean isAttacking(BaseBoard board, Position from, Position to);

    /**
     * Obtém todas as possíveis posições de movimento para esta peça a partir de uma posição dada.
     * <p>
     * Leva em consideração o estado atual do tabuleiro, incluindo outras peças.
     * <p>
     * Não verifica se o movimento colocaria o próprio rei em xeque.
     * 
     * @param board O tabuleiro de xadrez atual.
     * @param from A posição de origem da peça.
     * @return Uma lista de posições para as quais a peça pode se mover.
      */
    public abstract List<Position> getPossibleMoves(BaseBoard board, Position from);

    @Override
    public int hashCode() {
        return Objects.hash(color, getClass());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Piece other = (Piece) obj;
        return this.color == other.color;
    }

}
