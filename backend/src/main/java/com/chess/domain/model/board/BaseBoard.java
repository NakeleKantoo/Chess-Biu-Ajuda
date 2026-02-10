package com.chess.domain.model.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.chess.domain.model.base.Color;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.piece.Piece;
import com.chess.domain.utils.CloneUtils;
import com.chess.domain.utils.FenParser;

public abstract class BaseBoard {
    // Método protegido para acesso direto ao array (sem clone) para uso interno eficiente
    protected abstract Piece[][] getInternalSquares();
    protected abstract Map<Color, List<Position>> getInternalPiecesPositionsByColor();

    public abstract BoardState getBoardState();
    public abstract Color getCurrentPlayer();
    public abstract Position getEnPassantTarget();
    public abstract CastlingControl getCastlingControl();
    public abstract int getFullMoveClock();
    public abstract int getHalfMoveClock();
    
    // Posições dos Reis
    public abstract Position getKingPosition(Color color);

    // --- Lógica Compartilhada (Reutilizada por Board e BoardBuilder) ---

    /**
     * Retorna a peça na posição especificada.
     */
    public Piece getPieceAt(Position position) {
        Objects.requireNonNull(position, "A posição não pode ser nula");
        // Usa o método abstrato para acessar a matriz da subclasse
        return getInternalSquares()[position.getRow()][position.getCol()];
    }

    /**
     * Verifica se há uma peça na posição, com filtro opcional de cor.
     */
    public boolean hasPieceAt(Position position, Color color) {
        Objects.requireNonNull(position, "A posição não pode ser nula");

        Piece piece = getPieceAt(position);

        if (piece == null) {
            return false;
        }

        if (color == null) {
            return true;
        } 
        
        return piece.getColor().equals(color);
    }

    public boolean hasPieceAt(Position position) {
        return hasPieceAt(position, null);
    }

    /**
     * Retorna uma CÓPIA SEGURA da matriz de peças para uso externo (API pública).
     */
    public Piece[][] getSquares() {
        return CloneUtils.cloneSquares(getInternalSquares());
    }

    /**
     * Retorna uma cópia segura do mapa de posições das peças por cor.
     * 
     * @return um mapa clonado das posições das peças por cor.
      */
    public Map<Color, List<Position>> getPiecesPositionsByColor() {
        return CloneUtils.clonePiecesPositionsByColor(getInternalPiecesPositionsByColor());
    }

    public boolean canCastleKingSide(Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");
        // Note que aqui passamos 'this', assumindo que CastlingControl aceite BaseBoard
        return getCastlingControl().canCastleKingSide(this, color);
    }

    public boolean canCastleQueenSide(Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");
        return getCastlingControl().canCastleQueenSide(this, color);
    }

    public List<Position> getPiecesPositions(Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");
        List<Position> positions = getPiecesPositionsByColor().get(color);
        return new ArrayList<>(positions);
    }

    public String toFen() {
        return FenParser.boardToFen(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(getInternalSquares()), getCurrentPlayer(), getEnPassantTarget(), getCastlingControl());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        BaseBoard other = (BaseBoard) obj;
        return  Arrays.deepEquals(getInternalSquares(), other.getInternalSquares()) &&
                getCurrentPlayer() == other.getCurrentPlayer() &&
                Objects.equals(getEnPassantTarget(), other.getEnPassantTarget()) &&
                Objects.equals(getCastlingControl(), other.getCastlingControl());
    }

}
