package com.chess.entity.base;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Position {
    // Coordenadas da posição
    private final int row;
    private final int col;

    // Cache de todas as posições possíveis no tabuleiro
    private final static Position[][] positions = new Position[8][8];

    // Notação das colunas do tabuleiro
    private static final String[] COLUMNS = {"a", "b", "c", "d", "e", "f", "g", "h"};

    // Inicializa o cache de posições
    static {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                positions[i][j] = new Position(i, j);
            }
        }
    }

    private Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Retorna uma instância de Posição a partir de coordenadas de linha e coluna.
     *
     * @param row A linha (0-7).
     * @param col A coluna (0-7).
     * @return  {@code Position} A instância de Posição correspondente,
     *          {@code null} se as coordenadas estiverem fora do tabuleiro.
     */
    public static Position at(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return null;
        }
        return positions[row][col];
    }

    /**
     * Retorna uma instância de Posição a partir da notação do xadrez.
     *
     * @param string A notação da posição (ex: "a1", "h8").
     * @return {@code Position} A instância de Posição correspondente.
     * @throws IllegalArgumentException se a notação for nula, vazia ou inválida.
     */
    public static Position at(String string) {
        Objects.requireNonNull(string, "Notação de posição não pode ser nula.");
        if (string.trim().isEmpty()) {
            throw new IllegalArgumentException("Notação de posição não pode ser nula ou vazia.");
        }

        // Valida a notação da posição
        Pattern pattern = Pattern.compile("^[a-h][1-8]$");
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Formato de notação de posição inválido: \"" + string + "\"");
        }

        // Converte a notação para coordenadas de linha e coluna
        int row = '8' - string.charAt(1);
        int col = string.charAt(0) - 'a';

        return positions[row][col];
    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }

    /**
     * Verifica se as posições estão próximas
     * a no máximo 1 casa, inclui diagonal.
     * 
     * @param position {@code Position} para comparar
     * @return {@code true} se são próximas, {@code false} se não
      */
    public boolean isNear(Position position) {
        if (position == null) return false;

        return  Math.abs(this.row - position.row) <= 1 &&
                Math.abs(this.col - position.col) <= 1;
    }

    /**
     * Método que busca a próxima posição na direção.
     * 
     * @param direction {@code Direction} direção
     * @return {@code Position} se for possível andar na direção, {@code null} se não
      */
    public Position getNext(Direction direction) {
        Objects.requireNonNull(direction, "A direção não pode ser nula.");

        // Soma os valores do vetor com as coordenadas da posição
        int row = this.getRow() + direction.getX();
        int col = this.getCol() + direction.getY();

        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return null;
        }

        return positions[row][col];
    }
    
    @Override
    public String toString() {
        return (COLUMNS[col] + (8 - row));
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position other = (Position) obj;
        return  Objects.equals(this.row, other.row) &&
                Objects.equals(this.col, other.col);
    }
    
}

