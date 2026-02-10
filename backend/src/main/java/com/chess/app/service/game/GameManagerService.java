package com.chess.app.service.game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.chess.api.dto.response.game.PendingGameDTO;
import com.chess.domain.model.base.Color;
import com.chess.domain.model.game.GameConfig;
import com.chess.domain.model.game.PendingGame;
import com.chess.infrastructure.persistence.entity.GameEntity;

@Service
public class GameManagerService {

    private static final Logger log = LoggerFactory.getLogger(GameManagerService.class);

    private final GameTimerManager timerManager;
    private final GamePersistenceService gamePersistenceService;
    
    private final Map<UUID, GameSession> activeGames;
    private final Map<String, PendingGame> pendingGames;

    public GameManagerService(GameTimerManager timerManager, GamePersistenceService gamePersistenceService) {
        this.timerManager = timerManager;
        this.gamePersistenceService = gamePersistenceService;
        this.activeGames = new ConcurrentHashMap<>();
        this.pendingGames = new ConcurrentHashMap<>();
    }

    public PendingGameDTO createGame(GameConfig config, UUID creatorId) {
        UUID gameId = UUID.randomUUID();
        String gameCode = generateGameCode();
        
        pendingGames.put(gameCode, new PendingGame(gameId, creatorId, config));

        log.info("Jogo pendente criado: {} (código: {}, criador: {})",
                gameId, gameCode, creatorId);

        return new PendingGameDTO(gameId, gameCode);
    }

    public UUID joinGame(String gameCode, UUID joiningPlayerId) {
        PendingGame pendingGame = pendingGames.remove(gameCode);
        if (pendingGame == null) {
            throw new IllegalArgumentException("Jogo não encontrado para o código: " + gameCode);
        }

        log.info("Usuário {} está se juntando ao jogo com código {}",
                joiningPlayerId, gameCode);

        UUID whitePlayerId = pendingGame.config().getStartingColor() == Color.WHITE ? pendingGame.creatorId() : joiningPlayerId;
        UUID blackPlayerId = whitePlayerId == joiningPlayerId ? pendingGame.creatorId() : joiningPlayerId;

        if (whitePlayerId.equals(blackPlayerId)) {
            throw new IllegalArgumentException("O criador do jogo não pode se juntar como oponente.");
        }

        GameSession gameSession = new GameSession(pendingGame.config(), whitePlayerId, blackPlayerId, this, pendingGame.gameId(), timerManager);
        activeGames.put(pendingGame.gameId(), gameSession);

        log.info("Jogo {} iniciado com sucesso (white: {}, black: {})", 
                pendingGame.gameId(), whitePlayerId, blackPlayerId);

        return pendingGame.gameId();
    }

    public GameSession getGameSession(UUID gameId) {
        GameSession gameSession = activeGames.get(gameId);
        if (gameSession == null) {
            log.warn("Jogo {} não encontrado nas sessões ativas", gameId);
            throw new IllegalArgumentException("Jogo não encontrado para o ID: " + gameId);
        }
        return gameSession;
    }

    public void saveGame(GameSession gameSession) {
        UUID sessionId = gameSession.getSessionId();
        
        try {
            gamePersistenceService.saveCompletedGame(
                gameSession.getGame(),
                gameSession.getWhitePlayerId(),
                gameSession.getBlackPlayerId(),
                sessionId
            );
            
            activeGames.remove(sessionId);
            log.info("Jogo {} salvo e removido das sessões ativas", sessionId);
            
        } catch (Exception e) {
            log.error("Erro ao salvar jogo {}. Mantendo na memória para retry.", sessionId, e);
        }
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

    public GameEntity getSavedGameEntity(UUID gameId) {
        return gamePersistenceService.getGameById(gameId);
    }

}
