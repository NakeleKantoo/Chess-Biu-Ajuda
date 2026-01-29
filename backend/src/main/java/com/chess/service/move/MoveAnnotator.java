package com.chess.service.move;

import java.util.List;

import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.move.Move;
import com.chess.entity.move.MoveBuilder;
import com.chess.entity.move.MoveContext;
import com.chess.entity.piece.King;
import com.chess.entity.piece.Pawn;
import com.chess.entity.piece.Piece;

public class MoveAnnotator {

    public void annotateMoves(MoveContext moveContext, List<Move> legalMoves) {
        int moveCount = moveContext.getMoveCount();
        for (int i = 0; i < moveCount; i++) {
            Board newBoard = moveContext.getResultBoard(i);
            MoveBuilder moveBuilder = moveContext.getMoveBuilder(i);
            
            updateDesambiguationFlags(moveBuilder, legalMoves);
            updateMoveState(moveBuilder, newBoard);

            legalMoves.set(i, moveBuilder.build());
        }
    }

    private void updateDesambiguationFlags(MoveBuilder moveBuilder, List<Move> legalMoves) {
        // Se já tiver as flags setadas (por exemplo em testes ou manual), retorna
        if (moveBuilder.getColDesambiguation() || moveBuilder.getRowDesambiguation()) {
            return;
        }

        Piece movedPiece = moveBuilder.getMovedPiece();
        // Peões e Reis nunca precisam de desambiguação na notação algébrica padrão
        // (Reis não se repetem, Peões usam captura explicita cxd5 ou apenas d4)
        if ((movedPiece instanceof King) || (movedPiece instanceof Pawn)) {
            return;
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
    }

    private void updateMoveState(MoveBuilder moveBuilder, Board newBoard) {
        // Usa o construtor protected otimizado (sem contexto) para verificar checkmate/stalemate
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
    }
}
