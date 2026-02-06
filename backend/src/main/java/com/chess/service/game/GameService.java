package com.chess.service.game;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.board.Board;
import com.chess.entity.board.BoardBuilder;
import com.chess.entity.game.Game;
import com.chess.entity.game.GameBuilder;
import com.chess.entity.game.GameEndReason;
import com.chess.entity.game.GameState;
import com.chess.entity.move.Move;
import com.chess.entity.piece.Piece;
import com.chess.service.move.MoveExecutor;
import com.chess.service.move.MoveValidator;
import com.chess.utils.BoardStateUtils;

public class GameService {
    
    private Game game;

    private MoveExecutor moveExecutor = new MoveExecutor();

    private Timer timer = new Timer();

    private TimerTask startTask;
    private TimerTask endTask;

    private Color drawOffer;

    public GameService() {
        this.game = GameBuilder.standardRapidGame();
        scheduleAutoStart();
    }

    public Game getGame() { return game;}
    public Board getCurrentBoard() { return game.getCurrentBoard(); }
    public Color getCurrentPlayer() { return game.getCurrentBoard().getCurrentPlayer(); }
    public long getTimeRemaining(Color color) { return game.getTimeRemaining(color);  }
    public GameState getGameState() { return game.getGameState(); }
    public GameEndReason getGameEndReason() { return game.getGameEndReason(); }

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

    public boolean isActive() {
        return game.getGameState() == GameState.ACTIVE;
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
    public synchronized void makeMove(Position from, Position to, Piece promotionPiece) {
        Objects.requireNonNull(from, "Posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "Posição de destino não pode ser nula.");

        if (!isActive()) {
            throw new IllegalStateException("O jogo não está ativo. Não é possível fazer movimentos.");
        }

        if (startTask != null) {
            startTask.cancel();
            startTask = null;
        }

        MoveValidator validator = MoveValidator.of(game.getCurrentBoard());
        Move move = validator.createMove(from, to, promotionPiece);

        BoardBuilder nextBoardBuilder = moveExecutor.executeMove(game.getCurrentBoard(), move);
        List<Board> boardHistory = game.getBoardHistory();
        
        // Validar estado do BoardBuilder e instanciar Board
        nextBoardBuilder.setBoardState(BoardStateUtils.evaluateState(nextBoardBuilder, boardHistory));
        Board nextBoard = nextBoardBuilder.build();

        game.commitMove(move, nextBoard);

        if (isActive()) {
            this.drawOffer = null; 
            configTimerToEnd();
        } else {
            stopTimer();
        }
    }

    public void resign(Color resigningPlayer) {
        Objects.requireNonNull(resigningPlayer, "A cor do jogador que desiste não pode ser nula.");

        if (!isActive()) {
            throw new IllegalStateException("O jogo não está ativo. Não é possível desistir.");
        }

        stopTimer();
        game.resign(resigningPlayer);
    }

    public void offerDraw(Color player) {
        if (!isActive()) {
            throw new IllegalStateException("O jogo não está ativo. Não é possível oferecer empate.");
        }

        if (drawOffer == player.opposite()) {
            acceptDraw(player);
        } else {
            this.drawOffer = player;
        }
    }

    public void acceptDraw(Color player) {
        if (!isActive()) {
            throw new IllegalStateException("O jogo não está ativo. Não é possível aceitar empate.");
        }

        if (drawOffer != player.opposite()) {
            throw new IllegalStateException("Não há oferta de empate para o jogador " + player);
        }

        stopTimer();
        game.drawByAgreement();
    }

    private void stopTimer() {
        if (startTask != null) {
            startTask.cancel();
            startTask = null;
        }
        if (endTask != null) {
            endTask.cancel();
            endTask = null;
        }
        timer.cancel();
    }

    private void configTimerToEnd() {
        if (endTask != null) endTask.cancel();

        this.endTask = new TimerTask() {
            @Override
            public void run() {
                game.stopClock();
                timeout();
                // TODO: Notificar fim de jogo (Timeout)
            }
        };

        // Agendar a tarefa para encerrar o relógio após o tempo restante do jogador atual
        long timeRemaining = game.getTimeRemaining(getCurrentPlayer());
        timer.schedule(endTask, Math.max(timeRemaining, 1));
    }

    private synchronized void timeout() {
        Color playerWhoRanOutOfTime = getCurrentPlayer();
        Color opponent = playerWhoRanOutOfTime.opposite();

        // Verifico se o VENCEDOR (por tempo) tem material suficiente para vencer
        boolean opponentHasMatingMaterial = BoardStateUtils.hasMatingMaterial(getCurrentBoard(), opponent);

        if (opponentHasMatingMaterial) {
            game.timeoutLoss(playerWhoRanOutOfTime);
        } else {
            game.timeoutDraw();
        }
    }

}
