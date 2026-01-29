package com.chess.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.game.Move;
import com.chess.entity.game.MoveBuilder;
import com.chess.entity.game.MoveContext;
import com.chess.entity.piece.Piece;

public class MoveValidator {
    
    private final MoveExecutor moveExecutor;
    private final Board board;
    private final List<Move> legalMoves;

    private final MoveContext moveContext;

    public static MoveValidator of(Board board) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");

        MoveValidator validator = new MoveValidator(board, true);
        new MoveAnnotator().annotateMoves(validator.moveContext, validator.legalMoves);

        return validator;
    }

    protected MoveValidator(Board board) {
        this(board, false);
    }

    private MoveValidator(Board board, boolean createContext) {
        this.moveExecutor = new MoveExecutor();
        this.board = board;
        this.legalMoves = new ArrayList<>();
        this.moveContext = createContext ? new MoveContext() : null;

        generateLegalMoves();
    }

    public List<Move> getLegalMoves() {
        return new ArrayList<>(legalMoves);
    }

    public Board getBoard() {
        return board;
    }

    public boolean isMoveLegal(Move move) {
        return legalMoves.contains(move);
    }

    private void generateLegalMoves() {
        List<Position> piecesPositions = board.getPiecesPositions(board.getCurrentPlayer());

        for (Position pos : piecesPositions) {
            generatePieceMoves(pos);
        }
    }

    private void generatePieceMoves(Position from) {
        Piece piece = board.getPieceAt(from);
        List<Position> possibleMoves = piece.getPossibleMoves(board, from);

        for (Position to : possibleMoves) {
            validateMove(from, to, piece);
        }
    }

    private void validateMove(Position from, Position to, Piece piece) {
        MoveBuilder moveBuilder = new MoveBuilder(from, to, piece);

        MoveRules.applyCaptureRule(board, to, moveBuilder);
        MoveRules.applyCastleRule(board, from, to, piece, moveBuilder);
        MoveRules.applyEnPassantRule(board, from, to, piece, moveBuilder);

        if (MoveRules.isPromotion(to, piece)) {
            addPromotionMoves(moveBuilder);
        } else {
            addMove(moveBuilder);
        }

    }

    private void addPromotionMoves(MoveBuilder builder) {
        char[] promotionPieces = {'D', 'T', 'B', 'C'};
        for (char c : promotionPieces) {
            // Verifica a peça da cor para criar
            c = board.getCurrentPlayer().isWhite() ? c : Character.toLowerCase(c);
            Piece promotionPiece = Piece.create(c);

            // Adiciona a peça de promoção ao movimento
            builder.promotionPiece(promotionPiece);
            
            // Cria a instância de Move e adiciona à lista de movimentos legais se válido
            addMove(builder);
        }
    }

    private void addMove(MoveBuilder moveBuilder) {
        Move move = moveBuilder.build();
        Board newBoard = moveExecutor.executeMove(board, move);

        boolean movePutsOwnKingInCheck = newBoard.isInCheck(board.getCurrentPlayer());

        if (!movePutsOwnKingInCheck) {
            legalMoves.add(move);

            if (moveContext != null) moveContext.addMove(moveBuilder, newBoard);
        }
    }

}
