package com.chess.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.chess.entity.base.Direction;
import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.board.CastlingControl;
import com.chess.entity.game.Move;
import com.chess.entity.game.MoveBuilder;
import com.chess.entity.piece.King;
import com.chess.entity.piece.Pawn;
import com.chess.entity.piece.Piece;

public class MoveValidator {
    
    private final MoveExecutor moveExecutor;
    private final Board board;
    private final List<Move> legalMoves;

    public static MoveValidator of(Board board) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");

        MoveValidator validator = new MoveValidator(board);
        validator.verifyMoveConditions();

        return validator;
    }

    protected MoveValidator(Board board) {
        this.moveExecutor = new MoveExecutor();
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

    // Parte 1

    private void validateMove(Position from, Position to, Piece piece) {
        MoveBuilder moveBuilder = new MoveBuilder(from, to, piece);

        capturedPiece(from, to, moveBuilder);
        castle(from, to, piece, moveBuilder);
        enPassant(from, to, piece, moveBuilder);

        if (isPromotion(to, piece)) {
            verifyPromotions(from, to, moveBuilder);
        } else {
            buildAndAddMove(moveBuilder);
        }

    }

    private void capturedPiece(Position from, Position to, MoveBuilder moveBuilder) {
        Piece capturedPiece = board.getPieceAt(to);
        if (capturedPiece != null) {
            moveBuilder.capturedPiece(capturedPiece);
        }
    }

    private void castle(Position from, Position to, Piece piece, MoveBuilder moveBuilder) {
        boolean isCastling = piece instanceof King && !from.isNear(to); // Problema quando implementar variante 960
        if (!isCastling) {
            return;
        }

        boolean isKingSide = to.getCol() > from.getCol();
        boolean isWhite = board.getCurrentPlayer().isWhite();
        CastlingControl castlingControl = board.getCastlingControl();
        Position rookFrom;

        if (isWhite) {
            if (isKingSide) {
                rookFrom = Position.at(7, castlingControl.getWhiteKingSideRookCol());
            } else {
                rookFrom = Position.at(7, castlingControl.getWhiteQueenSideRookCol());
            }
        } else {
            if (isKingSide) {
                rookFrom = Position.at(0, castlingControl.getBlackKingSideRookCol());
            } else {
                rookFrom = Position.at(0, castlingControl.getBlackQueenSideRookCol());
            }
        }

        moveBuilder.castling(rookFrom);
    }

    private void enPassant(Position from, Position to, Piece piece, MoveBuilder moveBuilder) {
        boolean isPawn = piece instanceof Pawn;
        if (!isPawn) {
            return;
        }

        Direction dir = Direction.get(from, to);
        if (dir.isDiagonal() && !board.hasPieceAt(to)) {
            Piece capturedPiece = board.getPieceAt(Position.at(from.getRow(), to.getCol()));
            moveBuilder.capturedPiece(capturedPiece);
            moveBuilder.enPassant();
        }
    }

    private boolean isPromotion(Position to, Piece piece) {
        if (!(piece instanceof Pawn)) {
            return false;
        }

        int promotionRow = piece.getColor().isWhite() ? 0 : 7;
        return to.getRow() == promotionRow;
    }

    private void verifyPromotions(Position from, Position to, MoveBuilder builder) {
        char[] promotionPieces = {'D', 'T', 'B', 'C'};
        for (char c : promotionPieces) {
            // Verifica a peça da cor para criar
            c = board.getCurrentPlayer().isWhite() ? c : Character.toLowerCase(c);
            Piece promotionPiece = Piece.create(c);

            // Adiciona a peça de promoção ao movimento
            builder.promotionPiece(promotionPiece);
            
            // Cria a instância de Move e adiciona à lista de movimentos legais se válido
            buildAndAddMove(builder);
        }
    }


    private void buildAndAddMove(MoveBuilder moveBuilder) {
        Move move = moveBuilder.build();
        Board newBoard = moveExecutor.executeMove(board, move);

        if (!newBoard.isInCheck(board.getCurrentPlayer())) {
            legalMoves.add(move);
        }
    }

    // Parte 2

    private void verifyMoveConditions() {
        for (int i = 0; i < legalMoves.size(); i++) {
            Move move = legalMoves.get(i);
            Board newBoard = moveExecutor.executeMove(board, move);
            MoveBuilder moveBuilder = new MoveBuilder(move);
            
            moveBuilder = updateDesambiguationFlags(moveBuilder);
            moveBuilder = updateMoveState(moveBuilder, newBoard);

            legalMoves.set(i, moveBuilder.build());
        }
    }

    private MoveBuilder updateDesambiguationFlags(MoveBuilder moveBuilder) {
        // Se já tiver as flags setadas (por exemplo em testes ou manual), retorna
        if (moveBuilder.getColDesambiguation() || moveBuilder.getRowDesambiguation()) {
            return moveBuilder;
        }

        Piece movedPiece = moveBuilder.getMovedPiece();
        // Peões e Reis nunca precisam de desambiguação na notação algébrica padrão
        // (Reis não se repetem, Peões usam captura explicita cxd5 ou apenas d4)
        if ((movedPiece instanceof King) || (movedPiece instanceof Pawn)) {
            return moveBuilder;
        }

        Position to = moveBuilder.getTo();
        Position from = moveBuilder.getFrom();
        boolean sameCol = false;
        boolean sameRow = false;
        boolean otherPieceCanMove = false;

        // Verifica outros movimentos legais para encontrar conflitos
        for (Move move : legalMoves) { 
            // Ignora o mesmo movimento (origem igual)
            if (move.getFrom().equals(from)) {
                continue;
            }

            // Verifica se outra peça DO MESMO TIPO e MESMA COR pode ir para o MESMO DESTINO
            if (move.getTo().equals(to) && 
                move.getMovedPiece().getClass().equals(movedPiece.getClass()) &&
                move.getMovedPiece().getColor().equals(movedPiece.getColor())) {
                
                otherPieceCanMove = true;

                if (move.getFrom().getCol() == from.getCol()) {
                    sameCol = true;
                }
                if (move.getFrom().getRow() == from.getRow()) {
                    sameRow = true;
                }
            }
        }

        if (otherPieceCanMove) {
            // Regra 1: Se estiverem em colunas diferentes, desambigua pela coluna (ex: Ngf3)
            if (!sameCol) {
                moveBuilder.needColDesambiguation();
            }
            // Regra 2: Se estiverem na mesma coluna, desambigua pela linha (ex: N5f3)
            else if (!sameRow) {
                moveBuilder.needRowDesambiguation();
            }
            // Regra 3: Se (teoricamente) estiverem mesma col e linha (promoção múltipla?), ambas.
            else {
                moveBuilder.needColDesambiguation();
                moveBuilder.needRowDesambiguation();
            }
        }
        
        return moveBuilder;
    }

    private MoveBuilder updateMoveState(MoveBuilder moveBuilder, Board newBoard) {
        MoveValidator validator = new MoveValidator(newBoard);

        if (validator.getLegalMoves().isEmpty()) {
            if (newBoard.isInCheck(newBoard.getCurrentPlayer())) {
                moveBuilder.checkmate();
            } else {
                moveBuilder.stalemate();
            }
        } else {
            if (newBoard.isInCheck(newBoard.getCurrentPlayer())) {
                moveBuilder.check();
            }
        }

        return moveBuilder;
    }

    

}
