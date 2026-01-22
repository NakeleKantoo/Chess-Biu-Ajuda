package com.chess.entity.board;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.piece.Piece;

public class BoardBuilder {
    
    // Matriz 8x8 que representa as casas do tabuleiro e as peças contidas nelas.
    private Piece[][] squares;

    // Estado do tabuleiro
    private BoardState boardState;
    
    // A cor do jogador que tem a vez de jogar nesta posição.
    private Color currentPlayer;
    
    // Posição alvo para captura en passant, se aplicável.
    private Position enPassantTarget;

    // Controle de direitos de roque para ambos os jogadores.
    private CastlingControl castlingControl;

    // Posições dos reis para ambos os jogadores.
    private Position whiteKingPosition;
    private Position blackKingPosition;

    // Mapeamento das posições das peças por cor.
    private Map<Color, Set<Position>> piecesPositionsByColor;

    public static final char[] standardLinePieces = {'T', 'C', 'B', 'D', 'R', 'B', 'C', 'T'};

    public BoardBuilder() {
        this.squares = new Piece[8][8];
        this.piecesPositionsByColor = new HashMap<>();
        piecesPositionsByColor.put(Color.WHITE, new HashSet<>());
        piecesPositionsByColor.put(Color.BLACK, new HashSet<>());
    }

    public Piece[][] getSquares() {
        Piece[][] clonedSquares = new Piece[8][];
        for (int row = 0; row < 8; row++) {
            clonedSquares[row] = squares[row].clone();
        }
        return clonedSquares;
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public CastlingControl getCastlingControl() {
        return castlingControl;
    }

    public Position getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public Position getBlackKingPosition() {
        return blackKingPosition;
    }

    public Map<Color, Set<Position>> getPiecesPositionsByColor() {
        Map<Color, Set<Position>> clonedMap = new HashMap<>();
        for (Map.Entry<Color, Set<Position>> entry : piecesPositionsByColor.entrySet()) {
            clonedMap.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return clonedMap;
    }

    /**
     * Configura o tabuleiro com a disposição padrão das peças.
     * 
     * @return um objeto {@code Board} representando o tabuleiro configurado.
      */
    public Board buildStandard() {
        char[] linePieces = standardLinePieces;

        setRowPieces(Color.WHITE, 7, linePieces);
        setRowPawns(Color.WHITE, 6);
        setRowPawns(Color.BLACK, 1);
        setRowPieces(Color.BLACK, 0, linePieces);

        this.boardState = BoardState.IN_PROGRESS;
        this.currentPlayer = Color.WHITE;
        this.enPassantTarget = null;
        this.castlingControl = CastlingControl.INIT;
        this.whiteKingPosition = Position.at(7, 4);
        this.blackKingPosition = Position.at(0, 4);

        return new Board(this);
    }

    private void setRowPieces(Color color, int row, char[] linePieces) {
        for (int col = 0; col < 8; col++) {
            char pieceChar = linePieces[col];
            pieceChar = color.isWhite() ? Character.toUpperCase(pieceChar) : Character.toLowerCase(pieceChar);

            Piece piece = Piece.create(pieceChar);
            Position position = Position.at(row, col);

            placePiece(piece, position);
        }
    }

    private void setRowPawns(Color color, int row) {
        for (int col = 0; col < 8; col++) {
            char pawnChar = color.isWhite() ? 'P' : 'p';
            
            Piece pawn = Piece.create(pawnChar);
            Position position = Position.at(row, col);

            placePiece(pawn, position);
        }
    }

    private void placePiece(Piece piece, Position position) {
        int row = position.getRow();
        int col = position.getCol();

        Piece existingPiece = squares[row][col];
        if (existingPiece != null) {
            removePiece(position);
        }

        squares[row][col] = piece;
        piecesPositionsByColor.get(piece.getColor()).add(position);

        char pieceType = Character.toUpperCase(piece.getSymbol());
        if (pieceType == 'R') {
            if (piece.getColor().isWhite()) {
                whiteKingPosition = position;
            } else {
                blackKingPosition = position;
            }
        }
    }

    private void removePiece(Position position) {
        int row = position.getRow();
        int col = position.getCol();

        Piece existingPiece = squares[row][col];
        if (existingPiece != null) {
            piecesPositionsByColor.get(existingPiece.getColor()).remove(position);
            squares[row][col] = null;
        }
    }

}
