package com.chess.entity.board;

import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.piece.Piece;
import com.chess.utils.CloneUtils;

public class Board {

    // Matriz 8x8 que representa as casas do tabuleiro e as peças contidas nelas.
    private final Piece[][] squares;

    // Estado do tabuleiro
    private final BoardState boardState;
    
    // A cor do jogador que tem a vez de jogar nesta posição.
    private final Color currentPlayer;
    
    // Posição alvo para captura en passant, se aplicável.
    private final Position enPassantTarget;

    // Controle de direitos de roque para ambos os jogadores.
    private final CastlingControl castlingControl;

    // Posições dos reis para ambos os jogadores.
    private final Position whiteKingPosition;
    private final Position blackKingPosition;

    // Mapeamento das posições das peças por cor.
    protected final Map<Color, List<Position>> piecesPositionsByColor;

    // Número do movimento (incrementado após a jogada das pretas).
    private final int fullMoveClock;

    // Contador de meio-movimentos (para a regra dos 50 movimentos).
    private final int halfMoveClock;

    protected Board(BoardBuilder builder) {
        this.squares = builder.getSquares();
        this.boardState = builder.getBoardState();
        this.currentPlayer = builder.getCurrentPlayer();
        this.enPassantTarget = builder.getEnPassantTarget();
        this.castlingControl = builder.getCastlingControl();
        this.whiteKingPosition = builder.getWhiteKingPosition();
        this.blackKingPosition = builder.getBlackKingPosition();
        this.piecesPositionsByColor = builder.getPiecesPositionsByColor();
        this.fullMoveClock = builder.getFullMoveClock();
        this.halfMoveClock = builder.getHalfMoveClock();
    }

    /**
     * Retorna a peça na posição especificada.
     * 
     * @param position a posição no tabuleiro.
     * @return a {@code Piece} na posição, ou {@code null} se a posição estiver vazia.
      */
    public Piece getPieceAt(Position position) {
        Objects.requireNonNull(position, "A posição não pode ser nula");

        return squares[position.getRow()][position.getCol()];
    }

    /**
     * Verifica se há uma peça na posição especificada, opcionalmente filtrando pela cor.
     * 
     * @param position a posição no tabuleiro.
     * @param color a cor da peça a ser verificada, ou {@code null} para qualquer cor.
     * @return {@code true} se houver uma peça na posição (e da cor especificada, se fornecida), {@code false} caso contrário.
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

    /**
     * Verifica se há uma peça na posição especificada.
     * 
     * @param position a posição no tabuleiro.
     * @return {@code true} se houver uma peça na posição, {@code false} caso contrário.
      */
    public boolean hasPieceAt(Position position) {
        return hasPieceAt(position, null);
    }

    public Piece[][] getSquares() { return CloneUtils.cloneSquares(squares); }
    public BoardState getBoardState() { return boardState; }
    public Color getCurrentPlayer() { return currentPlayer; }
    public Position getEnPassantTarget() { return enPassantTarget; }
    public CastlingControl getCastlingControl() { return castlingControl; }
    public Map<Color, List<Position>> getPiecesPositionsByColor() { return CloneUtils.clonePiecesPositionsByColor(piecesPositionsByColor); }
    public int getFullMoveClock() { return fullMoveClock; }
    public int getHalfMoveClock() { return halfMoveClock; }

    public boolean canCastleKingSide(Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");
        
        return castlingControl.canCastleKingSide(this, color);
    }

    public boolean canCastleQueenSide(Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");

        return castlingControl.canCastleQueenSide(this, color);
    }

    public Position getKingPosition(Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");

        return color.isWhite() ? whiteKingPosition : blackKingPosition;
    }

    public List<Position> getPiecesPositions(Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");

        List<Position> positions = piecesPositionsByColor.get(color);

        return new ArrayList<>(positions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Estado: " + boardState.getDescription() + "\n");
        sb.append("Jogador atual: " + currentPlayer + "\n\n");
        for (int row = 0; row < 8; row++) {
            sb.append(8 - row).append("     ");
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                sb.append(piece != null ? piece.getSymbol() : '.');
                sb.append(' ');
            }
            sb.append('\n');
        }
        sb.append("\n      a b c d e f g h");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(squares), currentPlayer, enPassantTarget, castlingControl);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Board other = (Board) obj;
        return  Arrays.deepEquals(squares, other.squares) &&
                currentPlayer == other.currentPlayer &&
                Objects.equals(enPassantTarget, other.enPassantTarget) &&
                Objects.equals(castlingControl, other.castlingControl);
    }

    

}
