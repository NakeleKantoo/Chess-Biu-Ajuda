package com.chess.service.game;

import java.util.Objects;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.game.Game;
import com.chess.entity.move.Move;
import com.chess.entity.piece.Piece;
import com.chess.service.move.MoveExecutor;
import com.chess.service.move.MoveValidator;

public class GameService {
    
    private Game game;

    private MoveExecutor moveExecutor;

    public GameService() {
        this.game = Game.startNewStandardGame();
        this.moveExecutor = new MoveExecutor();
    }

    public Game getGame() {
        return game;
    }

    public Board getCurrentBoard() {
        return game.getCurrentBoard();
    }

    public Color getCurrentPlayerColor() {
        return game.getCurrentBoard().getCurrentPlayer();
    }

    /**
     * Realiza um movimento no jogo atual a partir de duas posições.
     * 
     * @param from Posição de origem do movimento.
     * @param to Posição de destino do movimento.
      */
    public void makeMove(Position from, Position to) {
        makeMove(from, to, null);
    }

    /**
     * Realiza um movimento no jogo atual a partir de duas posições
     * e uma peça de promoção opcional.
     * 
     * @param from Posição de origem do movimento.
     * @param to Posição de destino do movimento.
     * @param promotionPiece Peça para promoção, se aplicável.
      */
    public void makeMove(Position from, Position to, Piece promotionPiece) {
        Objects.requireNonNull(from, "Posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "Posição de destino não pode ser nula.");

        MoveValidator validator = MoveValidator.of(game.getCurrentBoard());
        Move move = validator.createMove(from, to, promotionPiece);
        Board nextBoard = moveExecutor.executeMove(game.getCurrentBoard(), move);

        game.commitMove(move, nextBoard);
    }

    /**
     * Desfaz o último movimento realizado no jogo atual.
      */
    public void undoLastMove() {
        this.game.undoLastMove();
    }

    /**
     * Reinicia o jogo para o estado inicial padrão.
      */
    public void resetGame() {
        this.game = Game.startNewStandardGame();
    }

}
