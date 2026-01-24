package com.chess.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.piece.Piece;

public class CloneUtils {

    public static Map<Color, Set<Position>> clonePiecesPositionsByColor(Map<Color, Set<Position>> piecesPositionsByColor) {
        Map<Color, Set<Position>> clonedMap = new HashMap<>();
        for (Map.Entry<Color, Set<Position>> entry : piecesPositionsByColor.entrySet()) {
            clonedMap.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return clonedMap;
    }

    public static Piece[][] cloneSquares(Piece[][] squares) {
        Piece[][] clonedSquares = new Piece[8][];
        for (int row = 0; row < 8; row++) {
            clonedSquares[row] = squares[row].clone();
        }
        return clonedSquares;
    }

}
