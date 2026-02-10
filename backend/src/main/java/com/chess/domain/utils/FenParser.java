package com.chess.domain.utils;

import com.chess.domain.model.base.Position;
import com.chess.domain.model.board.BaseBoard;
import com.chess.domain.model.board.CastlingControl;
import com.chess.domain.model.piece.Piece;

public class FenParser {
    
    public static String boardToFen(BaseBoard board) {
        StringBuilder fenBuilder = new StringBuilder();

        appendBoard(board, fenBuilder);
        appendCurrentPlayer(board, fenBuilder);
        appendCastlingRights(board, fenBuilder);
        appendEnPassantTarget(board, fenBuilder);
        appendHalfMoveClock(board, fenBuilder);
        appendFullMoveClock(board, fenBuilder);

        return fenBuilder.toString();
    }

    private static void appendBoard(BaseBoard board, StringBuilder fenBuilder) {
        int emptyCount = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position position = Position.at(row, col);
                Piece piece = board.getPieceAt(position);

                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fenBuilder.append(emptyCount);
                        emptyCount = 0;
                    }
                    
                    fenBuilder.append(piece.getSymbol());
                }
            }

            if (emptyCount > 0) {
                fenBuilder.append(emptyCount);
                emptyCount = 0;
            }

            fenBuilder.append(row < 7 ? '/' : ' ');
        }
    }

    private static void appendCurrentPlayer(BaseBoard board, StringBuilder fenBuilder) {
        fenBuilder.append(board.getCurrentPlayer().isWhite() ? "w " : "b ");
    }

    private static void appendCastlingRights(BaseBoard board, StringBuilder fenBuilder) {
        CastlingControl castlingControl = board.getCastlingControl();
        boolean anyCastle = false;
        
        if (castlingControl.getWhiteKingCol() != null) {

            if (castlingControl.getWhiteKingSideRookCol() != null) {
                fenBuilder.append('K');
                anyCastle = true;
            } 

            if (castlingControl.getWhiteQueenSideRookCol() != null) {
                fenBuilder.append('Q');
                anyCastle = true;
            }

        }

        if (castlingControl.getBlackKingCol() != null) {
            
            if (castlingControl.getBlackKingSideRookCol() != null) {
                fenBuilder.append('k');
                anyCastle = true;
            } 

            if (castlingControl.getBlackQueenSideRookCol() != null) {
                fenBuilder.append('q');
                anyCastle = true;
            }

        }

        if (!anyCastle) {
            fenBuilder.append('-');
        }

        fenBuilder.append(' ');
    }

    private static void appendEnPassantTarget(BaseBoard board, StringBuilder fenBuilder) {
        Position enPassantTarget = board.getEnPassantTarget();

        if (enPassantTarget != null) {
            fenBuilder.append(enPassantTarget.toString());
        } else {
            fenBuilder.append('-');
        }

        fenBuilder.append(' ');
    }

    private static void appendHalfMoveClock(BaseBoard board, StringBuilder fenBuilder) {
        fenBuilder.append(board.getHalfMoveClock()).append(' ');
    }

    private static void appendFullMoveClock(BaseBoard board, StringBuilder fenBuilder) {
        fenBuilder.append(board.getFullMoveClock());
    }

}
