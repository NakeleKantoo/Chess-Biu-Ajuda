package com.chess.app.session.game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.chess.domain.event.GameFinishedEvent;
import com.chess.domain.exception.game.GameSessionNotFoundException;
import com.chess.domain.exception.game.PendingGameNotFoundException;
import com.chess.domain.exception.player.SelfJoinGameException;
import com.chess.domain.model.game.GameConfig;
import com.chess.domain.model.game.PendingGame;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameSessionManager {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final GameTimerManager timerManager;
    
    private final Map<UUID, GameSession> activeGames = new ConcurrentHashMap<>();
    private final Map<String, PendingGame> pendingGames = new ConcurrentHashMap<>();

    public PendingGame createGame(GameConfig config, UUID creatorId) {
        UUID gameId = UUID.randomUUID();
        String gameCode = generateGameCode();
        PendingGame pendingGame = new PendingGame(gameId, creatorId, config, gameCode);
        
        pendingGames.put(gameCode, pendingGame);

        return pendingGame;
    }

    public PendingGame joinGame(String gameCode, UUID joiningPlayerId) {
        PendingGame pendingGame = pendingGames.remove(gameCode);
        if (pendingGame == null) {
            throw new PendingGameNotFoundException(gameCode);
        }
        if (joiningPlayerId.equals(pendingGame.creatorId())) {
            throw new SelfJoinGameException(joiningPlayerId);
        }

        UUID whitePlayerId, blackPlayerId;
        if (pendingGame.config().getCreatorColor().isWhite()) {
            whitePlayerId = pendingGame.creatorId();
            blackPlayerId = joiningPlayerId;
        } else {
            whitePlayerId = joiningPlayerId;
            blackPlayerId = pendingGame.creatorId();
        }

        GameSession gameSession = new GameSession(pendingGame.config(), whitePlayerId, blackPlayerId, pendingGame.gameId(), timerManager, this::onGameFinished);
        activeGames.put(pendingGame.gameId(), gameSession);

        return pendingGame;
    }

    public GameSession getGameSession(UUID sessionId) {
        GameSession gameSession = activeGames.get(sessionId);

        if (gameSession == null) {
            throw new GameSessionNotFoundException(sessionId);
        }

        return gameSession;
    }

    private String generateGameCode() {
        // Gerar um código de 6 caracteres (letras maiúsculas e números)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();

        // Garantir que o código seja único (não exista na lista de jogos pendentes)
        do {
            code.setLength(0); // Limpa o StringBuilder para gerar um novo código

            // Gerar um código aleatório de 6 caracteres
            for (int i = 0; i < 6; i++) {
                int index = (int) (Math.random() * chars.length());
                code.append(chars.charAt(index));
            }
        } while (pendingGames.containsKey(code.toString()));

        return code.toString();
    }

    private void onGameFinished(GameSession gameSession) {
        activeGames.remove(gameSession.getSessionId());
        applicationEventPublisher.publishEvent(new GameFinishedEvent(gameSession));
    }

}
