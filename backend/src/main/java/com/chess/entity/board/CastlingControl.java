package com.chess.entity.board;

import java.util.Objects;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.move.Move;
import com.chess.entity.piece.King;
import com.chess.entity.piece.Piece;
import com.chess.entity.piece.Rook;

public class CastlingControl {
    private final Integer whiteKingCol;
    private final Integer blackKingCol;

    private final Integer whiteKingSideRookCol;
    private final Integer whiteQueenSideRookCol;

    private final Integer blackKingSideRookCol;
    private final Integer blackQueenSideRookCol;

    public static final CastlingControl INIT = new CastlingControl(4, 4, 7, 0, 7, 0);

    public CastlingControl(Integer whiteKingCol, Integer blackKingCol, Integer whiteKingSideRookCol, Integer whiteQueenSideRookCol, Integer blackKingSideRookCol, Integer blackQueenSideRookCol) {
        this.whiteKingCol = whiteKingCol;
        this.blackKingCol = blackKingCol;
        this.whiteKingSideRookCol = whiteKingSideRookCol;
        this.whiteQueenSideRookCol = whiteQueenSideRookCol;
        this.blackKingSideRookCol = blackKingSideRookCol;
        this.blackQueenSideRookCol = blackQueenSideRookCol;
    }

    public Integer getWhiteKingCol() { return whiteKingCol; }
    public Integer getBlackKingCol() { return blackKingCol; }
    public Integer getWhiteKingSideRookCol() { return whiteKingSideRookCol; }
    public Integer getWhiteQueenSideRookCol() { return whiteQueenSideRookCol; }
    public Integer getBlackKingSideRookCol() { return blackKingSideRookCol; }
    public Integer getBlackQueenSideRookCol() { return blackQueenSideRookCol; }

    /**
     * Verifica se o jogador da cor especificada pode realizar o roque do lado do rei.
     * 
     * @param color a cor do jogador.
     * @return {@code true} se o jogador pode realizar o roque do lado do rei, {@code false} caso contrário.
      */
    public boolean canCastleKingSide(BaseBoard board, Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");
        boolean rightsActive;
        int row, rookCol;
        int finalKingCol = 6; // G1/G8

        if (color.isWhite()) {
            rightsActive = whiteKingCol != null && whiteKingSideRookCol != null;
            row = 7;
            rookCol = rightsActive ? whiteKingSideRookCol : -1;
        } else {
            rightsActive = blackKingCol != null && blackKingSideRookCol != null;
            row = 0;
            rookCol = rightsActive ? blackKingSideRookCol : -1;
        }

        if (!rightsActive) return false;
        
        Position kingPos = board.getKingPosition(color);
        Position rookPos = Position.at(row, rookCol);
        Position kingDest = Position.at(row, finalKingCol);

        // 1. Caminho Livre: Valida se não há peças entre Rei e Torre (exceto eles mesmos)
        boolean isPathClear = BoardAnalyzer.isPathClear(board, kingPos, rookPos);
        
        // 2. Caminho Seguro: Valida se o REi não passa por casa atacada
        boolean isPathSafe = !BoardAnalyzer.isPathUnderAttack(board, kingPos, kingDest, color.opposite());

        return isPathClear && isPathSafe;
    }

    /**
     * Verifica se o jogador da cor especificada pode realizar o roque do lado da dama.
     * 
     * @param color a cor do jogador.
     * @return {@code true} se o jogador pode realizar o roque do lado da dama, {@code false} caso contrário.
      */
    public boolean canCastleQueenSide(BaseBoard board, Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");
        boolean rightsActive;
        int row, rookCol;
        int finalKingCol = 2; // C1/C8

        if (color.isWhite()) {
            rightsActive = whiteKingCol != null && whiteQueenSideRookCol != null;
            row = 7;
            rookCol = rightsActive ? whiteQueenSideRookCol : -1;
        } else {
            rightsActive = blackKingCol != null && blackQueenSideRookCol != null;
            row = 0;
            rookCol = rightsActive ? blackQueenSideRookCol : -1;
        }
        
        if (!rightsActive) return false;

        Position kingPos = board.getKingPosition(color);
        Position rookPos = Position.at(row, rookCol);
        Position kingDest = Position.at(row, finalKingCol);

        boolean isPathClear = BoardAnalyzer.isPathClear(board, kingPos, rookPos);
        boolean isPathSafe = !BoardAnalyzer.isPathUnderAttack(board, kingPos, kingDest, color.opposite());

        return isPathClear && isPathSafe;
    }

    /**
     * Atualiza o controle de direitos de roque com base no movimento realizado.
     * 
     * @param move o movimento realizado.
     * @return uma nova instância de {@code CastlingControl} refletindo o estado atualizado dos direitos de roque.
      */
    public CastlingControl update(Move move) {
        Objects.requireNonNull(move, "O movimento não pode ser nulo.");

        Integer nWhiteKingCol = this.whiteKingCol;
        Integer nBlackKingCol = this.blackKingCol;
        Integer nSameWhiteKingSideRookCol = this.whiteKingSideRookCol;
        Integer nSameWhiteQueenSideRookCol = this.whiteQueenSideRookCol;
        Integer nSameBlackKingSideRookCol = this.blackKingSideRookCol;
        Integer nSameBlackQueenSideRookCol = this.blackQueenSideRookCol;

        Piece movedPiece = move.getMovedPiece();
        Position from = move.getFrom();
        Position to = move.getTo();

        // 1. Lógica de Movimento (Rei ou Torre moveu)
        if (movedPiece instanceof King) {
            if (movedPiece.getColor().isWhite()) {
                nWhiteKingCol = null;
            } else {
                nBlackKingCol = null;
            }
        } else if (movedPiece instanceof Rook) {
            if (movedPiece.getColor().isWhite()) {
                if (matches(from, 7, nSameWhiteKingSideRookCol)) nSameWhiteKingSideRookCol = null;
                else if (matches(from, 7, nSameWhiteQueenSideRookCol)) nSameWhiteQueenSideRookCol = null;
            } else {
                if (matches(from, 0, nSameBlackKingSideRookCol)) nSameBlackKingSideRookCol = null;
                else if (matches(from, 0, nSameBlackQueenSideRookCol)) nSameBlackQueenSideRookCol = null;
            }
        }

        // 2. Lógica de Captura (Torre capturada no local de origem)
        // Se a posição de destino de qualquer movimento for a casa de uma torre rastreada, o direito é perdido.
        
        // Verifica Torres Brancas (linha 7)
        if (matches(to, 7, nSameWhiteKingSideRookCol)) nSameWhiteKingSideRookCol = null;
        if (matches(to, 7, nSameWhiteQueenSideRookCol)) nSameWhiteQueenSideRookCol = null;

        // Verifica Torres Pretas (linha 0)
        if (matches(to, 0, nSameBlackKingSideRookCol)) nSameBlackKingSideRookCol = null;
        if (matches(to, 0, nSameBlackQueenSideRookCol)) nSameBlackQueenSideRookCol = null;

        return new CastlingControl(nWhiteKingCol, nBlackKingCol, nSameWhiteKingSideRookCol, nSameWhiteQueenSideRookCol, nSameBlackKingSideRookCol, nSameBlackQueenSideRookCol);
    }

    private boolean matches(Position pos, int row, Integer col) {
        return col != null && pos.getRow() == row && pos.getCol() == col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(whiteKingCol, blackKingCol, whiteKingSideRookCol, whiteQueenSideRookCol, blackKingSideRookCol, blackQueenSideRookCol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CastlingControl that = (CastlingControl) o;
        return  Objects.equals(whiteKingCol, that.whiteKingCol) &&
                Objects.equals(blackKingCol, that.blackKingCol) &&
                Objects.equals(whiteKingSideRookCol, that.whiteKingSideRookCol) &&
                Objects.equals(whiteQueenSideRookCol, that.whiteQueenSideRookCol) &&
                Objects.equals(blackKingSideRookCol, that.blackKingSideRookCol) &&
                Objects.equals(blackQueenSideRookCol, that.blackQueenSideRookCol);
    }

    @Override
    public String toString() {
        return "CastlingControl{" +
                "WK=" + whiteKingCol + ", BK=" + blackKingCol +
                ", WKR=" + whiteKingSideRookCol + ", WQR=" + whiteQueenSideRookCol +
                ", BKR=" + blackKingSideRookCol + ", BQR=" + blackQueenSideRookCol +
                '}';
    }
}
