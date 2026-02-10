package com.chess.app.service.game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chess.api.dto.response.game.PendingGameDTO;
import com.chess.domain.model.base.Color;
import com.chess.domain.model.game.GameConfig;
import com.chess.domain.model.game.PendingGame;
import com.chess.infrastructure.persistence.entity.GameEntity;
import com.chess.infrastructure.persistence.mapper.GamePersistenceMapper;
import com.chess.infrastructure.repository.GameRepository;
import com.chess.infrastructure.repository.UserRepository;

@Service
public class GameManagerService {

    @Autowired private GamePersistenceMapper gameMapper;
    @Autowired private GameRepository gameRepository;
    @Autowired private UserRepository userRepository;
    
    // Onde tudo fica guardado
    private final Map<UUID, GameSession> activeGames = new ConcurrentHashMap<>();

    private final Map<String, PendingGame> pendingGames = new ConcurrentHashMap<>();

    // Cria uma nova partida e devolve o ID (a chave do apartamento)
    public PendingGameDTO createGame(GameConfig config, UUID creatorId) {
        UUID gameId = UUID.randomUUID();
        String gameCode = generateGameCode();
        
        pendingGames.put(gameCode, new PendingGame(gameId, creatorId, config));

        return new PendingGameDTO(gameId, gameCode);
    }

    public UUID joinGame(String gameCode, UUID joiningPlayerId) {
        PendingGame pendingGame = pendingGames.remove(gameCode);
        if (pendingGame == null) {
            throw new IllegalArgumentException("Jogo não encontrado para o código: " + gameCode);
        }

        UUID whitePlayerId = pendingGame.config().getStartingColor() == Color.WHITE ? pendingGame.creatorId() : joiningPlayerId;
        UUID blackPlayerId = whitePlayerId == joiningPlayerId ? pendingGame.creatorId() : joiningPlayerId;

        if (whitePlayerId.equals(blackPlayerId)) {
            throw new IllegalArgumentException("O criador do jogo não pode se juntar como oponente.");
        }

        GameSession gameService = new GameSession(pendingGame.config(), whitePlayerId, blackPlayerId, this, pendingGame.gameId());
        activeGames.put(pendingGame.gameId(), gameService);

        return pendingGame.gameId();
    }

    // Busca a partida para jogar
    public GameSession getGameService(UUID gameId) {
        GameSession service = activeGames.get(gameId);
        if (service == null) throw new IllegalArgumentException("Jogo não encontrado para o ID: " + gameId);
        return service;
    }

    public void saveGame(GameSession gameSession) {
        gameSession.save(gameRepository, gameMapper, userRepository);
        activeGames.remove(gameSession.getSessionId());
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
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Jogo salvo não encontrado para o ID: " + gameId));
    }

}
