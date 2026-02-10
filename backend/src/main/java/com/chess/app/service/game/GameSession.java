package com.chess.app.service.game;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.chess.app.service.move.MoveExecutor;
import com.chess.app.service.move.MoveValidator;
import com.chess.domain.model.base.Color;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.board.Board;
import com.chess.domain.model.board.BoardBuilder;
import com.chess.domain.model.game.Game;
import com.chess.domain.model.game.GameBuilder;
import com.chess.domain.model.game.GameConfig;
import com.chess.domain.model.game.GameEndReason;
import com.chess.domain.model.game.GameState;
import com.chess.domain.model.move.Move;
import com.chess.domain.model.move.MoveBuilder;
import com.chess.domain.model.piece.Piece;
import com.chess.domain.utils.BoardStateUtils;
import com.chess.domain.utils.NotationUtils;
import com.chess.infrastructure.persistence.entity.GameEntity;
import com.chess.infrastructure.persistence.entity.UserEntity;
import com.chess.infrastructure.persistence.mapper.GameMapper;
import com.chess.infrastructure.repository.GameRepository;
import com.chess.infrastructure.repository.UserRepository;

public class GameSession {

    private UUID sessionId;
    
    private GameManagerService gameManager;
    private Game game;

    private MoveExecutor moveExecutor = new MoveExecutor();

    private Timer timer = new Timer();

    private TimerTask startTask;
    private TimerTask endTask;

    private final UUID whitePlayerId;
    private final UUID blackPlayerId;

    private UUID drawOfferPlayerId;

    private boolean saved = false;

    public GameSession(GameConfig config, UUID whitePlayerId, UUID blackPlayerId, GameManagerService manager, UUID sessionId) {
        this.game = GameBuilder.create(config.getGameType(), config.getTimeControl(), config.getStartingColor());
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
        this.gameManager = manager;
        this.sessionId = sessionId;
        scheduleAutoStart();
    }

    public Game getGame() { return game;}
    public Board getCurrentBoard() { return game.getCurrentBoard(); }
    public Color getCurrentPlayer() { return game.getCurrentBoard().getCurrentPlayer(); }
    public long getTimeRemaining(Color color) { return game.getTimeRemaining(color);  }
    public GameState getGameState() { return game.getGameState(); }
    public GameEndReason getGameEndReason() { return game.getGameEndReason(); }
    public UUID getSessionId() { return sessionId; }

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
    public void makeMove(Position from, Position to, UUID playerId) {
        makeMove(from, to, null, playerId);
    }

    /**
     * Realiza um movimento no jogo atual a partir de duas posições
     * e uma peça de promoção opcional.
     * 
     * @param from Posição de origem do movimento.
     * @param to Posição de destino do movimento.
     * @param promotionPiece Peça para promoção, se aplicável.
      */
    public synchronized void makeMove(Position from, Position to, Piece promotionPiece, UUID playerId) {
        Objects.requireNonNull(from, "Posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "Posição de destino não pode ser nula.");

        if (!isActive()) {
            throw new IllegalStateException("O jogo não está ativo. Não é possível fazer movimentos.");
        }

        if (!playerId.equals(getCurrentPlayerId())) {
            throw new IllegalStateException("Não é a vez do jogador com ID: " + playerId);
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

        String san = NotationUtils.toSan(move, game.getCurrentBoard(), nextBoard.getBoardState(), validator.getLegalMoves());
        move = new MoveBuilder(move).san(san).build();

        game.commitMove(move, nextBoard);

        if (isActive()) {
            this.drawOfferPlayerId = null; 
            configTimerToEnd();
        } else {
            stopTimer();
            gameManager.saveGame(this);
        }
    }

    public void resign(UUID resigningPlayer) {
        Objects.requireNonNull(resigningPlayer, "O ID do jogador que desiste não pode ser nulo.");

        if (!isActive()) {
            throw new IllegalStateException("O jogo não está ativo. Não é possível desistir.");
        }

        Color resigningColor = getPlayerColor(resigningPlayer);

        stopTimer();
        game.resign(resigningColor);
    }

    public void offerDraw(UUID offeringDrawPlayerId) {
        if (!isActive()) {
            throw new IllegalStateException("O jogo não está ativo. Não é possível oferecer empate.");
        }

        if (drawOfferPlayerId == getOpponentId(offeringDrawPlayerId)) {
            acceptDraw(offeringDrawPlayerId);
        } else {
            this.drawOfferPlayerId = offeringDrawPlayerId;
        }
    }

    public void acceptDraw(UUID acceptingDrawPlayerId) {
        if (!isActive()) {
            throw new IllegalStateException("O jogo não está ativo. Não é possível aceitar empate.");
        }

        if (drawOfferPlayerId != getOpponentId(acceptingDrawPlayerId)) {
            return;
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

    private Color getPlayerColor(UUID playerId) {
        if (playerId.equals(whitePlayerId)) {
            return Color.WHITE;
        } else if (playerId.equals(blackPlayerId)) {
            return Color.BLACK;
        } else {
            throw new IllegalArgumentException("O jogador com ID " + playerId + " não está participando deste jogo.");
        }
    }

    private UUID getOpponentId(UUID playerId) {
        if (playerId.equals(whitePlayerId)) {
            return blackPlayerId;
        } else if (playerId.equals(blackPlayerId)) {
            return whitePlayerId;
        } else {
            throw new IllegalArgumentException("O jogador com ID " + playerId + " não está participando deste jogo.");
        }
    }

    private UUID getCurrentPlayerId() {
        Color currentPlayerColor = getCurrentPlayer();
        return currentPlayerColor == Color.WHITE ? whitePlayerId : blackPlayerId;
    }

    public void save(GameRepository repo, GameMapper mapper, UserRepository userRepo) {
        if (this.saved) {
            throw new IllegalStateException("Este jogo já foi salvo. Não é possível salvar novamente.");
        }
        UserEntity white = userRepo.findById(whitePlayerId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário branco não encontrado para o ID: " + whitePlayerId));
        UserEntity black = userRepo.findById(blackPlayerId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário preto não encontrado para o ID: " + blackPlayerId));

        GameEntity entity = mapper.toEntity(this.game, white, black, this.sessionId);
        repo.save(entity);
        this.saved = true;
    }

}
