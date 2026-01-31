package com.chess.entity.base;

import java.util.Objects;

public class Direction {
    // Valores do vetor direção
    private final int x;
    private final int y;
    // Armazena se o vetor é diagonal
    private final boolean isDiagonal;

    // Constantes para todas as direções possíveis do vetor
    public static final Direction UP = new Direction(-1, 0);
    public static final Direction DOWN = new Direction(1, 0);
    public static final Direction RIGHT = new Direction(0, 1);
    public static final Direction LEFT = new Direction(0, -1);
    public static final Direction UP_RIGHT = new Direction(-1, 1);
    public static final Direction UP_LEFT = new Direction(-1, -1);
    public static final Direction DOWN_RIGHT = new Direction(1, 1);
    public static final Direction DOWN_LEFT = new Direction(1, -1);

    // Matriz para mapear componentes de vetor para direções
    private static final Direction[][] directions = new Direction[][] {
        {UP_LEFT,    UP,    UP_RIGHT},
        {LEFT,       null,  RIGHT},
        {DOWN_LEFT,  DOWN,  DOWN_RIGHT}
    };

    // Arrays para retornar conjuntos de direções
    private static final Direction[] ORTHOGONAL_DIRECTIONS = {UP, DOWN, LEFT, RIGHT};
    private static final Direction[] DIAGONAL_DIRECTIONS = {UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT};
    private static final Direction[] ALL_DIRECTIONS = {UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT};

    private Direction(int x, int y) {
        this.x = x;
        this.y = y;
        this.isDiagonal = (x != 0 && y != 0);
    }

    /**
     * Cria uma direção a partir de componentes de vetor x e y.
     *
     * @param x componente x (-1, 0, ou 1).
     * @param y componente y (-1, 0, ou 1).
     * @return A instância de Direção correspondente.
     * @throws IllegalArgumentException se os componentes do vetor forem inválidos.
     */
    public static Direction get(int x, int y) {
        if (Math.abs(x) > 1 || Math.abs(y) > 1 || (x == 0 && y == 0)) {
            throw new IllegalArgumentException("Componentes de direção inválidos: x=" + x + ", y=" + y);
        }
        return directions[x + 1][y + 1];
    }

    /**
     * Calcula a direção entre duas posições.
     * Retorna uma direção apenas se o movimento for em uma linha reta ou diagonal pura.
     *
     * @param from Posição de origem.
     * @param to   Posição de destino.
     * @return A Direção do movimento, ou {@code null} se o movimento não for linear/diagonal.
     * @throws IllegalArgumentException se as posições de origem ou destino forem nulas.
     */
    public static Direction get(Position from, Position to) {
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "A posição de destino não pode ser nula.");
        
        if (from.equals(to)) {
            return null; // Não há direção se as posições são as mesmas.
        }

        int deltaX = to.getRow() - from.getRow();
        int deltaY = to.getCol() - from.getCol();

        if (deltaX != 0 && deltaY != 0 && Math.abs(deltaX) != Math.abs(deltaY)) {
            return null; // Retorna null para movimentos não lineares/diagonais (ex: cavalo).
        }

        return Direction.get(
            (int) Math.signum(deltaX),
            (int) Math.signum(deltaY)
        );
    }

    /**
     * Retorna todas as direções ortogonais (horizontal/vertical)
     */
    public static Direction[] getOrthogonalDirections() {
        return ORTHOGONAL_DIRECTIONS;
    }

    /**
     * Retorna todas as direções diagonais
     */
    public static Direction[] getDiagonalDirections() {
        return DIAGONAL_DIRECTIONS;
    }

    /**
     * Retorna todas as direções (ortogonais + diagonais)
     */
    public static Direction[] getAllDirections() {
        return ALL_DIRECTIONS;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isDiagonal() {
        return isDiagonal;
    }

    public boolean isOrthogonal() {
        return !isDiagonal;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Direction other = (Direction) obj;
        return  Objects.equals(x, other.x) &&
                Objects.equals(y, other.y);
    }
}
