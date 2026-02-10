package com.chess.app.service.game;

import java.util.Objects;
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

public class GameSession {

    private static final long DEFAULT_AUTO_START_DELAY = 10000; // 10 segundos

    private UUID sessionId;
    
    private GameManagerService gameManager;
    private Game game;

    private MoveExecutor moveExecutor = new MoveExecutor();
    private final GameTimerManager timerManager;

    private final UUID whitePlayerId;
    private final UUID blackPlayerId;

    private UUID drawOfferPlayerId;

    public GameSession(GameConfig config, UUID whitePlayerId, UUID blackPlayerId, GameManagerService manager, UUID sessionId, GameTimerManager timerManager) {
        this.game = GameBuilder.create(config.getGameType(), config.getTimeControl(), config.getStartingColor());
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
        this.gameManager = manager;
        this.sessionId = sessionId;
        this.timerManager = timerManager;
        this.scheduleAutoStart();
    }

    public Game getGame() { return game;}
    public Board getCurrentBoard() { return game.getCurrentBoard(); }
    public Color getCurrentPlayerColor() { return game.getCurrentBoard().getCurrentPlayer(); }
    public long getTimeRemaining(Color color) { return game.getTimeRemaining(color);  }
    public GameState getGameState() { return game.getGameState(); }
    public GameEndReason getGameEndReason() { return game.getGameEndReason(); }
    public UUID getSessionId() { return sessionId; }
    public UUID getWhitePlayerId() { return whitePlayerId; }
    public UUID getBlackPlayerId() { return blackPlayerId; }

    private record MoveResult(Move move, Board nextBoard) {}

    public boolean isActive() {
        return game.getGameState() == GameState.ACTIVE;
    }

    private void validateActiveGame() {
        if (!isActive()) {
            throw new IllegalStateException("O jogo não está ativo. Ação não permitida.");
        }
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
        validateMoveRequest(from, to, playerId);

        MoveResult moveResult = createMove(from, to, promotionPiece);
        game.commitMove(moveResult.move(), moveResult.nextBoard());

        handlePostMove();
    }

    private void validateMoveRequest(Position from, Position to, UUID playerId) {
        Objects.requireNonNull(from, "Posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "Posição de destino não pode ser nula.");
        Objects.requireNonNull(playerId, "ID do jogador não pode ser nulo.");
        validateActiveGame();

        UUID currentPlayerId = PlayerSessionValidator.getCurrentPlayerId(getCurrentPlayerColor(), whitePlayerId, blackPlayerId);
        PlayerSessionValidator.validatePlayerTurn(playerId, currentPlayerId);

        timerManager.cancelAutoStart(sessionId);
    }

    private Board executeMoveAndBuildBoard(Move move) {
        Board currentBoard = game.getCurrentBoard();
        BoardBuilder nextBoardBuilder = moveExecutor.executeMove(currentBoard, move);
        nextBoardBuilder.setBoardState(
            BoardStateUtils.evaluateState(nextBoardBuilder, game.getBoardHistory())
        );
        return nextBoardBuilder.build();
    }

    private MoveResult createMove(Position from, Position to, Piece promotionPiece) {
        Board currentBoard = game.getCurrentBoard();
        MoveValidator validator = MoveValidator.of(currentBoard);
        Move move = validator.createMove(from, to, promotionPiece);

        Board nextBoard = executeMoveAndBuildBoard(move);
        String san = NotationUtils.toSan(move, currentBoard, nextBoard.getBoardState(), validator.getLegalMoves());

        Move validatedMove = new MoveBuilder(move).san(san).build();
        return new MoveResult(validatedMove, nextBoard);
    }

    private void handlePostMove() {
        if (isActive()) {
            this.drawOfferPlayerId = null; 
            this.scheduleTimeout();
        } else {
            finishGame();
        }
    }

    public void resign(UUID resigningPlayer) {
        Objects.requireNonNull(resigningPlayer, "O ID do jogador que desiste não pode ser nulo.");
        validateActiveGame();

        Color resigningColor = PlayerSessionValidator.getPlayerColor(resigningPlayer, whitePlayerId, blackPlayerId);
        game.resign(resigningColor);

        finishGame();
    }

    public void offerDraw(UUID offeringDrawPlayerId) {
        validateActiveGame();

        if (drawOfferMatchesOpponent(offeringDrawPlayerId)) {
            acceptDraw(offeringDrawPlayerId);
        } else {
            this.drawOfferPlayerId = offeringDrawPlayerId;
        }
    }

    public void acceptDraw(UUID acceptingDrawPlayerId) {
        validateActiveGame();

        if (!drawOfferMatchesOpponent(acceptingDrawPlayerId)) {
            return;
        }

        game.drawByAgreement();
        finishGame();
    }

    private boolean drawOfferMatchesOpponent(UUID offeringPlayerId) {
        UUID opponentId = PlayerSessionValidator.getOpponentId(offeringPlayerId, whitePlayerId, blackPlayerId);
        return drawOfferPlayerId != null && drawOfferPlayerId.equals(opponentId);
    }

    private synchronized void handleAutoStart() {
        if (!isActive()) return;

        game.startClock();
        this.scheduleTimeout();
    }

    private synchronized void handleTimeout() {
        if (!isActive()) return;

        Color playerWhoRanOutOfTime = getCurrentPlayerColor();
        Color opponent = playerWhoRanOutOfTime.opposite();
        boolean opponentHasMatingMaterial = BoardStateUtils.hasMatingMaterial(getCurrentBoard(), opponent);

        if (opponentHasMatingMaterial) {
            game.timeoutLoss(playerWhoRanOutOfTime);
        } else {
            game.timeoutDraw();
        }

        finishGame();
    }

    private void scheduleAutoStart() {
        timerManager.scheduleAutoStart(sessionId, this::handleAutoStart, DEFAULT_AUTO_START_DELAY);
    }

    private void scheduleTimeout() {
        timerManager.scheduleTimeout(sessionId, this::handleTimeout, getTimeRemaining(getCurrentPlayerColor()));
    }

    private void finishGame() {
        timerManager.cancelAllTimers(sessionId);
        gameManager.saveGame(this);
    }

}
