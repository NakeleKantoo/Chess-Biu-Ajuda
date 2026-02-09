package com.chess.app.service.move;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.chess.domain.model.base.Position;
import com.chess.domain.model.board.BaseBoard;
import com.chess.domain.model.board.Board;
import com.chess.domain.model.board.BoardAnalyzer;
import com.chess.domain.model.board.BoardBuilder;
import com.chess.domain.model.move.Move;
import com.chess.domain.model.move.MoveBuilder;
import com.chess.domain.model.piece.Piece;

public class MoveValidator {
    
    private final Board board;
    private final List<Move> legalMoves;

    public static boolean hasAnyLegalMove(BaseBoard board) {
        List<Position> piecesPositions = board.getPiecesPositions(board.getCurrentPlayer());

        for (Position from : piecesPositions) {
            Piece piece = board.getPieceAt(from);
            List<Position> possibleMoves = piece.getPossibleMoves(board, from);

            for (Position to : possibleMoves) {
                MoveBuilder moveBuilder = new MoveBuilder(from, to, piece);

                applyRules(moveBuilder, board, from, to, piece);

                if (MoveRules.isPromotion(to, piece)) {
                    char c = board.getCurrentPlayer().isWhite() ? 'D' : 'd';
                    Piece promotionPiece = Piece.create(c);

                    // Adiciona a peça de promoção ao movimento
                    moveBuilder.promotionPiece(promotionPiece);
                }

                Move move = moveBuilder.build();
                
                if (!movePutsOwnKingInCheck(board, move)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static MoveValidator of(Board board) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");

        MoveValidator validator = new MoveValidator(board);

        return validator;
    }

    private MoveValidator(Board board) {
        this.board = board;
        this.legalMoves = new ArrayList<>();

        generateLegalMoves();
    }

    public List<Move> getLegalMoves() {
        return new ArrayList<>(legalMoves);
    }

    public Board getBoard() {
        return board;
    }

    public Move createMove(Position from, Position to, Piece promotionPiece) {
        Objects.requireNonNull(from, "Posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "Posição de destino não pode ser nula.");

        if (promotionPiece != null && !MoveRules.isPromotionPiece(promotionPiece)) {
            throw new IllegalArgumentException("Peça de promoção inválida: " + promotionPiece.getSymbol());
        }

        Piece movedPiece = board.getPieceAt(from);
        if (movedPiece == null) {
            throw new IllegalArgumentException("Não há peça na posição de origem: " + from);
        }

        MoveBuilder moveBuilder = new MoveBuilder(from, to, movedPiece);

        if (promotionPiece != null) {
            moveBuilder.promotionPiece(promotionPiece);
        }

        Move possibleMove = moveBuilder.build();

        if (!legalMoves.contains(possibleMove)) {
            throw new IllegalArgumentException("Movimento ilegal de " + from + " para " + to);
        }

        return legalMoves.get(legalMoves.indexOf(possibleMove));
    }

    private void generateLegalMoves() {
        if (board.getBoardState().isGameOver()) {
            return; // Se o jogo já acabou, não há movimentos legais
        }

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

        applyRules(moveBuilder, board, from, to, piece);

        if (MoveRules.isPromotion(to, piece)) {
            addPromotionMoves(moveBuilder);
        } else {
            addMove(moveBuilder);
        }

    }

    private void addPromotionMoves(MoveBuilder builder) {
        char[] promotionPieces = {'Q', 'R', 'B', 'N'};
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

        if (!movePutsOwnKingInCheck(board, move)) {
            legalMoves.add(move);
        }
    }

    private static void applyRules(MoveBuilder moveBuilder, BaseBoard board, Position from, Position to, Piece piece) {
        MoveRules.applyCaptureRule(board, to, moveBuilder);
        MoveRules.applyCastleRule(board, from, to, piece, moveBuilder);
        MoveRules.applyEnPassantRule(board, from, to, piece, moveBuilder);
    }

    private static boolean movePutsOwnKingInCheck(BaseBoard board, Move move) {
        // Se for um Builder (mutável), precisamos de um snapshot imutável 
        // para não corromper o estado original durante a simulação do movimento.
        Board boardToSimulate;
        if (board instanceof BoardBuilder) {
            boardToSimulate = ((BoardBuilder) board).build();
        } else {
            boardToSimulate = (Board) board;
        }

        BoardBuilder newBoard = new MoveExecutor().executeMove(boardToSimulate, move);

        return BoardAnalyzer.isInCheck(newBoard, board.getCurrentPlayer());
    }

}
