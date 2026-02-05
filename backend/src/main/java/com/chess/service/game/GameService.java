package com.chess.service.game;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.board.BoardBuilder;
import com.chess.entity.game.Game;
import com.chess.entity.move.Move;
import com.chess.entity.piece.Piece;
import com.chess.service.move.MoveExecutor;
import com.chess.service.move.MoveValidator;

public class GameService {
    
    private Game game;

    private MoveExecutor moveExecutor;

    private Timer timer;

    private TimerTask startTask;
    private TimerTask endTask;

    public GameService() {
        this.game = Game.startNewStandardGame();
        this.moveExecutor = new MoveExecutor();
        this.timer = new Timer();

        scheduleAutoStart();
    }

    public Game getGame() { return game;}
    public Board getCurrentBoard() { return game.getCurrentBoard(); }
    public Color getCurrentPlayer() { return game.getCurrentBoard().getCurrentPlayer(); }
    public long getTimeRemaining(Color color) { return game.getTimeRemaining(color);  }

    private void scheduleAutoStart() {
        this.startTask = new TimerTask() {
            @Override
            public void run() {
                // Se rodar, inicia o relógio automaticamente
                // IMPORTANTE: Sincronizar se necessário no futuro
                game.startClock();
                configTimerToEnd(); 
            }
        };
        // Agendar para 10 segundos
        timer.schedule(startTask, 10000);
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

        if (startTask != null) {
            startTask.cancel();
            startTask = null;
        }

        MoveValidator validator = MoveValidator.of(game.getCurrentBoard());
        Move move = validator.createMove(from, to, promotionPiece);
        BoardBuilder nextBoardBuilder = moveExecutor.executeMove(game.getCurrentBoard(), move);
        
        // Validar estado do BoardBuilder e instanciar Board
        nextBoardBuilder.setBoardState(BoardStateEvaluator.evaluateState(nextBoardBuilder, game));
        Board nextBoard = nextBoardBuilder.build();

        game.commitMove(move, nextBoard);

        configTimerToEnd();
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
        if (this.timer != null) {
            this.timer.cancel(); 
        }

        this.game = Game.startNewStandardGame();
        this.timer = new Timer();
        this.startTask = null;
        this.endTask = null;

        scheduleAutoStart();
    }

    private void configTimerToEnd() {
        if (endTask != null) endTask.cancel();

        this.endTask = new TimerTask() {
            @Override
            public void run() {
                game.stopClock();
                // TODO: Notificar fim de jogo (Timeout)
                System.out.println("TEMPO ACABOU PARA: " + getCurrentPlayer());
            }
        };

        // Agendar a tarefa para encerrar o relógio após o tempo restante do jogador atual
        long timeRemaining = game.getTimeRemaining(getCurrentPlayer());
        timer.schedule(endTask, Math.max(timeRemaining, 1));
    }

}
