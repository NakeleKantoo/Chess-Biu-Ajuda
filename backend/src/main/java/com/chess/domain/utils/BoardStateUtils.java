package com.chess.domain.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.chess.app.service.move.MoveValidator;
import com.chess.domain.model.base.Color;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.board.BaseBoard;
import com.chess.domain.model.board.Board;
import com.chess.domain.model.board.BoardAnalyzer;
import com.chess.domain.model.board.BoardState;
import com.chess.domain.model.piece.Bishop;
import com.chess.domain.model.piece.King;
import com.chess.domain.model.piece.Knight;
import com.chess.domain.model.piece.Pawn;
import com.chess.domain.model.piece.Piece;
import com.chess.domain.model.piece.Queen;
import com.chess.domain.model.piece.Rook;

public class BoardStateUtils {

    /**
     * Avalia e determina o estado do tabuleiro (Xeque, Mate, Empate, Em Andamento).
     * 
     * @param board O tabuleiro (ou builder) a ser analisado.
     * @param boardHistory Histórico dos tabuleiros anteriores para verificação de repetições.
     * @return O BoardState calculado.
     */
    public static BoardState evaluateState(BaseBoard board, List<Board> boardHistory) {
        // 1. Verificar regras automáticas de empate (50 lances, material)
        if (isInsufficientMaterial(board)) {
            return BoardState.DRAW_BY_INSUFFICIENT_MATERIAL;
        }

        if (isFiftyMoveRule(board)) {
            return BoardState.DRAW_BY_FIFTY_MOVE_RULE;
        }

        if (isThreefoldRepetition(board, boardHistory)) {
            return BoardState.DRAW_BY_THREEFOLD_REPETITION;
        }

        // 2. Verificar Xeque e Movimentos Legais
        return getCheckState(board);
    }

    public static BoardState getCheckState(BaseBoard board) {
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

    private static boolean isThreefoldRepetition(BaseBoard currentBoardStructure, List<Board> boardHistory) {
        // Como currentBoardStructure pode ser um Builder recém-modificado, precisamos ver se ele iguala aos anteriores.
        long repetitionCount = boardHistory.stream()
                .filter(historyBoard -> historyBoard.equals(currentBoardStructure))
                .count();

        return repetitionCount >= 2; // Se já apareceu 2 vezes, essa será a 3ª.
    }
    
    private static boolean hasAnyLegalMove(BaseBoard board) {
        return MoveValidator.hasAnyLegalMove(board);
    }

    /**
     * Verifica se o jogador possui material suficiente para dar mate.
     * Considera como verdadeiro se for possível dar mate com ajuda do adversário.
     * 
     * @param board O tabuleiro a ser avaliado.
     * @param player O jogador a ser avaliado.
     * @return {@code true} se o jogador possuir material para mate, {@code false} caso contrário.
      */
    public static boolean hasMatingMaterial(BaseBoard board, Color player) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(player, "O jogador não pode ser nulo.");

        List<Position> positions = board.getPiecesPositions(player);
        int playerPieces = positions.size();

        // Somente rei -> mate impossível
        if (playerPieces <= 1) {
            return false;
        }

        boolean hasKnight = false;
        Integer firstBishopColor = null;

        for (Position pos : positions) {
            Piece p = board.getPieceAt(pos);

            if (p instanceof King) {
                continue;
            }

            // Possui peão, torre ou dama -> mate possível
            if (p instanceof Queen || p instanceof Rook || p instanceof Pawn) {
                return true;
            }

            // Salva se tem cavalo
            if (p instanceof Knight) {
                hasKnight = true;
            } 

            // Se não é nenhuma outra peça, só pode ser bispo
            int colorSquare = (pos.getRow() + pos.getCol()) % 2;
            if (firstBishopColor == null) {
                firstBishopColor = colorSquare;
            } else if (firstBishopColor != colorSquare) {
                return true; // Bispos em cores diferentes -> mate possível
            }
        }

        List<Position> opponentPositions = board.getPiecesPositions(player.opposite());
        
        // Caso de K+N
        if (hasKnight) {
            // Se tenho mais de 2 peças (ex: K+N+N ou K+N+B), ganha sempre.
            // Se o oponente tiver mais de 1 peça, pode ajudar a dar mate.
            if (playerPieces > 2 || opponentPositions.size() > 1) {
                return true;
            } else {
                return false;
            }
        }

        // Caso de K+BBB... (bispos de mesmas cores)
        // Se oponente tiver bispo de cor diferente ou qualquer outra peça -> mate possível
        for (Position pos : opponentPositions) {
            Piece p = board.getPieceAt(pos);

            if (p instanceof King) {
                continue;
            }

            if (p instanceof Queen || p instanceof Rook || p instanceof Pawn || p instanceof Knight) {
                return true;
            }

            int colorSquare = (pos.getRow() + pos.getCol()) % 2;

            if (firstBishopColor != null && colorSquare != firstBishopColor) {
                return true; // Bispos em cores diferentes -> mate possível
            }
        }

        return false;
    }

}
