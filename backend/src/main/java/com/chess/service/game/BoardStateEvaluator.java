package com.chess.service.game;

import java.util.Arrays;
import java.util.List;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.board.BaseBoard;
import com.chess.entity.board.Board;
import com.chess.entity.board.BoardAnalyzer;
import com.chess.entity.board.BoardBuilder;
import com.chess.entity.board.BoardState;
import com.chess.entity.game.Game;
import com.chess.entity.piece.Bishop;
import com.chess.entity.piece.Knight;
import com.chess.entity.piece.Pawn;
import com.chess.entity.piece.Piece;
import com.chess.entity.piece.Queen;
import com.chess.entity.piece.Rook;
import com.chess.service.move.MoveValidator;

public class BoardStateEvaluator {

    /**
     * Avalia e determina o estado do tabuleiro (Xeque, Mate, Empate, Em Andamento).
     * 
     * @param board O tabuleiro (ou builder) a ser analisado.
     * @param game O jogo atual (para verificação de histórico).
     * @return O BoardState calculado.
     */
    public static BoardState evaluateState(BaseBoard board, Game game) {
        // 1. Verificar regras automáticas de empate (50 lances, material)
        if (isInsufficientMaterial(board)) {
            return BoardState.DRAW_BY_INSUFFICIENT_MATERIAL;
        }

        if (isFiftyMoveRule(board)) {
            return BoardState.DRAW_BY_FIFTY_MOVE_RULE;
        }

        if (isThreefoldRepetition(game, board)) {
            return BoardState.DRAW_BY_THREEFOLD_REPETITION;
        }

        // 2. Verificar Xeque e Movimentos Legais
        boolean isCheck = BoardAnalyzer.isInCheck(board, board.getCurrentPlayer());
        boolean hasLegalMoves = hasAnyLegalMove(board);

        if (isCheck) {
            return hasLegalMoves ? BoardState.CHECK : BoardState.CHECKMATE;
        } else {
            return hasLegalMoves ? BoardState.IN_PROGRESS : BoardState.STALEMATE;
        }
    }

    private static boolean isFiftyMoveRule(BaseBoard board) {
        return board.getHalfMoveClock() >= 100;
    }

    private static boolean isInsufficientMaterial(BaseBoard board) {
        List<Position> whitePos = board.getPiecesPositions(Color.WHITE);
        List<Position> blackPos = board.getPiecesPositions(Color.BLACK);
        
        int totalPieces = whitePos.size() + blackPos.size();

        // Regra rápida 1: Rei vs Rei
        if (totalPieces <= 2) {
            return true;
        }

        boolean hasKnight = false;
        Integer firstBishopColor = null;

        for (List<Position> list : Arrays.asList(whitePos, blackPos)) {
            for (Position pos : list) {
                Piece p = board.getPieceAt(pos);

                if (p instanceof Queen || p instanceof Rook || p instanceof Pawn) {
                    return false;
                }

                if (p instanceof Knight) {
                    hasKnight = true;
                } else if (p instanceof Bishop) {
                    int color = (pos.getRow() + pos.getCol()) % 2;
                    
                    if (firstBishopColor == null) {
                        firstBishopColor = color;
                    } else if (firstBishopColor != color) {
                        return false; // Bispos em cores diferentes -> Mate possível
                    }
                }
            }
        }

        // Regra rápida 2: Rei vs Rei + (Bispo ou Cavalo) - exatamente 3 peças
        if (totalPieces == 3) {
            return true;
        }

        // Se tiver Cavalo e mais de 3 peças (ex: KN vs KN ou KNN vs K), mate é possível
        if (hasKnight) {
            return false;
        }

        // Se chegou aqui:
        // 1. Não tem Q, R, P.
        // 2. Não tem (ou não importa) Cavalo pois count > 3 e se tivesse retornaria false acima? 
        //    Na verdade se tem cavalo e count > 3, retorna false. Se não tem cavalo, só sobram Bispos e Reis.
        // 3. Bispos (se houver) já foram verificados e estão todos na mesma cor.
        return true;
    }

    private static boolean isThreefoldRepetition(Game game, BaseBoard currentBoardStructure) {
        // Como currentBoardStructure pode ser um Builder recém-modificado, precisamos ver se ele iguala aos anteriores.
        long repetitionCount = game.getBoardHistory().stream()
                .filter(historyBoard -> historyBoard.equals(currentBoardStructure))
                .count();

        return repetitionCount >= 2; // Se já apareceu 2 vezes, essa será a 3ª.
    }
    
    private static boolean hasAnyLegalMove(BaseBoard board) {
        Board boardToCheck;
        
        if (board instanceof Board) {
            boardToCheck = (Board) board;
        } else if (board instanceof BoardBuilder) {
            boardToCheck = ((BoardBuilder) board).build();
        } else {
            throw new IllegalArgumentException("Tipo de tabuleiro não suportado");
        }
        
        return !MoveValidator.of(boardToCheck).getLegalMoves().isEmpty();
    }


}
