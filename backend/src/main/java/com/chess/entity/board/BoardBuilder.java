package com.chess.entity.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private Map<Color, List<Position>> piecesPositionsByColor;

    // Número do movimento (incrementado após a jogada das pretas).
    private int fullMoveClock;

    // Contador de meio-movimentos (para a regra dos 50 movimentos).
    private int halfMoveClock;

    public static final char[] standardLinePieces = {'T', 'C', 'B', 'D', 'R', 'B', 'C', 'T'};

    public BoardBuilder() {
        this.squares = new Piece[8][8];
        this.piecesPositionsByColor = new HashMap<>();
        piecesPositionsByColor.put(Color.WHITE, new ArrayList<>());
        piecesPositionsByColor.put(Color.BLACK, new ArrayList<>());
        this.fullMoveClock = 1;
        this.halfMoveClock = 0;
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
        this.fullMoveClock = board.getFullMoveClock();
        this.halfMoveClock = board.getHalfMoveClock();
    }

    public Piece[][] getSquares() { return CloneUtils.cloneSquares(squares); }
    public Piece getPieceAt(Position position) { return squares[position.getRow()][position.getCol()]; }
    public BoardState getBoardState() { return boardState; }
    public Color getCurrentPlayer() { return currentPlayer; }
    public Position getEnPassantTarget() { return enPassantTarget; }
    public CastlingControl getCastlingControl() { return castlingControl; }
    public Position getWhiteKingPosition() { return whiteKingPosition; }
    public Position getBlackKingPosition() { return blackKingPosition; }
    public Map<Color, List<Position>> getPiecesPositionsByColor() { return CloneUtils.clonePiecesPositionsByColor(piecesPositionsByColor); }
    public int getFullMoveClock() { return fullMoveClock; }
    public int getHalfMoveClock() { return halfMoveClock; }

    public void setBoardState(BoardState boardState) { this.boardState = boardState; }
    public void setCurrentPlayer(Color currentPlayer) { this.currentPlayer = currentPlayer; }
    public void setEnPassantTarget(Position enPassantTarget) { this.enPassantTarget = enPassantTarget; }
    public void setCastlingControl(CastlingControl castlingControl) { this.castlingControl = castlingControl; }
    public void setFullMoveClock(int fullMoveClock) { this.fullMoveClock = fullMoveClock; }
    public void setHalfMoveClock(int halfMoveClock) { this.halfMoveClock = halfMoveClock; }

    public void incrementFullMoveClock() { this.fullMoveClock++; }
    public void incrementHalfMoveClock() { this.halfMoveClock++; }
    public void resetHalfMoveClock() { this.halfMoveClock = 0; }

    /**
     * Configura o tabuleiro com a disposição padrão das peças.
     * 
     * @return um objeto {@code Board} representando o tabuleiro configurado.
      */
    public Board buildStandard() {
        char[] linePieces = standardLinePieces;
        squares = new Piece[8][8];
        piecesPositionsByColor.get(Color.WHITE).clear();
        piecesPositionsByColor.get(Color.BLACK).clear();

        setRowPieces(Color.WHITE, 7, linePieces);
        setRowPawns(Color.WHITE, 6);
        setRowPawns(Color.BLACK, 1);
        setRowPieces(Color.BLACK, 0, linePieces);

        this.boardState = BoardState.IN_PROGRESS;
        this.currentPlayer = Color.WHITE;
        this.enPassantTarget = null;
        this.castlingControl = CastlingControl.INIT;
        this.fullMoveClock = 1;
        this.halfMoveClock = 0;

        return new Board(this);
    }

    public Board buildTest() {
        char[] linePieces = standardLinePieces;
        squares = new Piece[8][8];
        piecesPositionsByColor.get(Color.WHITE).clear();
        piecesPositionsByColor.get(Color.BLACK).clear();

        setRowPieces(Color.WHITE, 7, linePieces);
        setRowPawns(Color.WHITE, 6);
        setRowPawns(Color.BLACK, 1);
        setRowPieces(Color.BLACK, 0, linePieces);

        this.boardState = BoardState.IN_PROGRESS;
        this.currentPlayer = Color.WHITE;
        this.enPassantTarget = null;
        this.castlingControl = CastlingControl.INIT;
        this.fullMoveClock = 1;
        this.halfMoveClock = 0;
        
        removePiece(Position.at("g2"));
        removePiece(Position.at("g8"));
        placePiece(Piece.create('P'), Position.at("g7"));

        return new Board(this);
    }

    /**
     * Constrói um objeto {@code Board} com base no estado atual do construtor.
     * Verifica se o estado do tabuleiro é válido antes de construir o objeto.
     * 
     * @return um objeto {@code Board} representando o tabuleiro configurado.
     * @throws IllegalStateException se o estado do tabuleiro for inválido.
      */
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
        if (hasMoreKings(Color.WHITE)) {
            throw new IllegalStateException("Mais de um rei branco encontrado.");
        }
        if (hasMoreKings(Color.BLACK)) {
            throw new IllegalStateException("Mais de um rei preto encontrado.");
        }
        if (fullMoveClock <= 0) {
            throw new IllegalStateException("O número do movimento deve ser maior que zero.");
        }
        if (halfMoveClock < 0) {
            throw new IllegalStateException("O contador de meio-movimentos não pode ser negativo.");
        }
        return new Board(this);
    }

    private boolean hasMoreKings(Color color) {
        List<Position> positions = piecesPositionsByColor.get(color);
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

    /**
     * Configura uma linha do tabuleiro com as peças especificadas.
     * 
     * @param color a cor das peças a serem colocadas.
     * @param row a linha do tabuleiro onde as peças serão colocadas.
     * @param linePieces um array de caracteres representando as peças na linha.
      */
    public void setRowPieces(Color color, int row, char[] linePieces) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");
        Objects.requireNonNull(linePieces, "As peças da linha não podem ser nulas.");

        if (row < 0 || row > 7) {
            throw new IllegalArgumentException("A linha deve estar entre 0 e 7.");
        }
        if (linePieces.length != 8) {
            throw new IllegalArgumentException("O array de peças da linha deve ter exatamente 8 elementos.");
        }


        for (int col = 0; col < 8; col++) {
            char pieceChar = linePieces[col];
            pieceChar = color.isWhite() ? Character.toUpperCase(pieceChar) : Character.toLowerCase(pieceChar);

            Piece piece = Piece.create(pieceChar);
            Position position = Position.at(row, col);

            placePiece(piece, position);
        }
    }

    /**
     * Configura uma linha do tabuleiro com peões da cor especificada.
     * 
     * @param color a cor dos peões a serem colocados.
     * @param row a linha do tabuleiro onde os peões serão colocados.
      */
    public void setRowPawns(Color color, int row) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");
        
        if (row < 0 || row > 7) {
            throw new IllegalArgumentException("A linha deve estar entre 0 e 7.");
        }

        for (int col = 0; col < 8; col++) {
            char pawnChar = color.isWhite() ? 'P' : 'p';
            
            Piece pawn = Piece.create(pawnChar);
            Position position = Position.at(row, col);

            placePiece(pawn, position);
        }
    }

    /**
     * Coloca uma peça na posição especificada no tabuleiro.
     * Não remove a peça da posição anterior, se aplicável.
     * Remove a peça existente na posição de destino, se houver.
     * Atualiza os estados relacionados, como a posição do rei e o mapeamento das posições das peças por cor.
     * 
     * @param piece a peça a ser colocada.
     * @param position a posição onde a peça será colocada.
      */
    public void placePiece(Piece piece, Position position) {
        Objects.requireNonNull(piece, "A peça não pode ser nula.");
        Objects.requireNonNull(position, "A posição não pode ser nula.");

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

    /**
     * Remove a peça da posição especificada no tabuleiro.
     * Atualiza os estados relacionados, como a posição do rei e o mapeamento das posições das peças por cor.
     * 
     * @param position a posição da peça a ser removida.
      */
    public void removePiece(Position position) {
        Objects.requireNonNull(position, "A posição não pode ser nula.");

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
