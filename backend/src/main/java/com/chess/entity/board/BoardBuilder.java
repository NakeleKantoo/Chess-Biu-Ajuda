package com.chess.entity.board;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.piece.King;
import com.chess.entity.piece.Piece;
import com.chess.utils.CloneUtils;

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

    public BoardBuilder(Board board) {
        this.squares = board.getSquares();
        this.boardState = board.getBoardState();
        this.currentPlayer = board.getCurrentPlayer();
        this.enPassantTarget = board.getEnPassantTarget();
        this.castlingControl = board.getCastlingControl();
        this.whiteKingPosition = board.getKingPosition(Color.WHITE);
        this.blackKingPosition = board.getKingPosition(Color.BLACK);
        this.piecesPositionsByColor = board.getPiecesPositionsByColor();
    }

    public Piece[][] getSquares() { return CloneUtils.cloneSquares(squares); }
    public BoardState getBoardState() { return boardState; }
    public Color getCurrentPlayer() { return currentPlayer; }
    public Position getEnPassantTarget() { return enPassantTarget; }
    public CastlingControl getCastlingControl() { return castlingControl; }
    public Position getWhiteKingPosition() { return whiteKingPosition; }
    public Position getBlackKingPosition() { return blackKingPosition; }
    public Map<Color, Set<Position>> getPiecesPositionsByColor() { return CloneUtils.clonePiecesPositionsByColor(piecesPositionsByColor); }

    public void setBoardState(BoardState boardState) { this.boardState = boardState; }
    public void setCurrentPlayer(Color currentPlayer) { this.currentPlayer = currentPlayer; }
    public void setEnPassantTarget(Position enPassantTarget) { this.enPassantTarget = enPassantTarget; }
    public void setCastlingControl(CastlingControl castlingControl) { this.castlingControl = castlingControl; }

    /**
     * Configura o tabuleiro com a disposição padrão das peças.
     * 
     * @return um objeto {@code Board} representando o tabuleiro configurado.
      */
    public Board buildStandard() {
        char[] linePieces = standardLinePieces;
        squares = new Piece[8][8];
        piecesPositionsByColor = new HashMap<>();
        piecesPositionsByColor.put(Color.WHITE, new HashSet<>());
        piecesPositionsByColor.put(Color.BLACK, new HashSet<>());

        setRowPieces(Color.WHITE, 7, linePieces);
        setRowPawns(Color.WHITE, 6);
        setRowPawns(Color.BLACK, 1);
        setRowPieces(Color.BLACK, 0, linePieces);

        this.boardState = BoardState.IN_PROGRESS;
        this.currentPlayer = Color.WHITE;
        this.enPassantTarget = null;
        this.castlingControl = CastlingControl.INIT;

        return new Board(this);
    }

    public Board build() {
        if (boardState == null) {
            throw new IllegalStateException("O estado do tabuleiro não foi definido.");
        }
        if (currentPlayer == null) {
            throw new IllegalStateException("O jogador atual não foi definido.");
        }
        if (castlingControl == null) {
            throw new IllegalStateException("O controle de roque não foi definido.");
        }
        if (whiteKingPosition == null) {
            throw new IllegalStateException("A posição do rei branco não foi definida.");
        }
        if (blackKingPosition == null) {
            throw new IllegalStateException("A posição do rei preto não foi definida.");
        }
        if (whiteKingPosition.isNear(blackKingPosition)) {
            throw new IllegalStateException("Os reis não podem estar em posições adjacentes.");
        }
        if (hasMoreKings(Color.WHITE)) {
            throw new IllegalStateException("Mais de um rei branco encontrado.");
        }
        if (hasMoreKings(Color.BLACK)) {
            throw new IllegalStateException("Mais de um rei preto encontrado.");
        }
        return new Board(this);
    }

    private boolean hasMoreKings(Color color) {
        Set<Position> positions = piecesPositionsByColor.get(color);
        boolean hasKing = false;
        for (Position position : positions) {
            Piece piece = squares[position.getRow()][position.getCol()];
            if (piece instanceof King) {
                if (hasKing) {
                    return true;
                }
                hasKing = true;
            }
        }
        return false;
    }

    public void setRowPieces(Color color, int row, char[] linePieces) {
        for (int col = 0; col < 8; col++) {
            char pieceChar = linePieces[col];
            pieceChar = color.isWhite() ? Character.toUpperCase(pieceChar) : Character.toLowerCase(pieceChar);

            Piece piece = Piece.create(pieceChar);
            Position position = Position.at(row, col);

            placePiece(piece, position);
        }
    }

    public void setRowPawns(Color color, int row) {
        for (int col = 0; col < 8; col++) {
            char pawnChar = color.isWhite() ? 'P' : 'p';
            
            Piece pawn = Piece.create(pawnChar);
            Position position = Position.at(row, col);

            placePiece(pawn, position);
        }
    }

    public void placePiece(Piece piece, Position position) {
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

    public void removePiece(Position position) {
        int row = position.getRow();
        int col = position.getCol();

        Piece existingPiece = squares[row][col];
        if (existingPiece != null) {
            piecesPositionsByColor.get(existingPiece.getColor()).remove(position);
            squares[row][col] = null;
            if (existingPiece instanceof King) {
                if (existingPiece.getColor().isWhite()) {
                    if (position.equals(whiteKingPosition)) whiteKingPosition = null;
                } else {
                    if (position.equals(blackKingPosition)) blackKingPosition = null;
                }
            }
        }
    }

}
