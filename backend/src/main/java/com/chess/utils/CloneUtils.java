package com.chess.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.piece.Piece;

public class CloneUtils {

    /**
     * Clona um mapeamento de posições das peças por cor.
     * 
     * @param piecesPositionsByColor Mapeamento original das posições das peças por cor.
     * @return Uma cópia do mapeamento fornecido.
      */
    public static Map<Color, Set<Position>> clonePiecesPositionsByColor(Map<Color, Set<Position>> piecesPositionsByColor) {
        Objects.requireNonNull(piecesPositionsByColor, "O mapeamento das posições das peças por cor não pode ser nulo.");
        if (piecesPositionsByColor.size() != 2) {
            throw new IllegalArgumentException("O mapeamento deve conter exatamente duas entradas para as cores das peças.");
        }

        Map<Color, Set<Position>> clonedMap = new HashMap<>();

        for (Map.Entry<Color, Set<Position>> entry : piecesPositionsByColor.entrySet()) {
            clonedMap.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        return clonedMap;
    }

    /**
     * Clona uma matriz bidimensional de peças do tabuleiro.
     * 
     * @param squares Matriz original 8x8 de peças do tabuleiro.
     * @return Uma cópia da matriz fornecida.
      */
    public static Piece[][] cloneSquares(Piece[][] squares) {
        Objects.requireNonNull(squares, "A matriz de casas do tabuleiro não pode ser nula.");
        if (squares.length != 8) {
            throw new IllegalArgumentException("A matriz de casas do tabuleiro deve ter 8 linhas.");
        }

        Piece[][] clonedSquares = new Piece[8][];

        for (int row = 0; row < 8; row++) {
            clonedSquares[row] = squares[row].clone();

            if (clonedSquares[row].length != 8) {
                throw new IllegalArgumentException("Cada linha da matriz de casas do tabuleiro deve ter 8 colunas.");
            }
        }

        return clonedSquares;
    }

}
