package com.chess.service.game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.chess.entity.game.GameConfig;

@Service
public class GameManager {
    
    // Onde tudo fica guardado
    private final Map<UUID, GameService> activeGames = new ConcurrentHashMap<>();

    // Cria uma nova partida e devolve o ID (a chave do apartamento)
    public UUID createGame(GameConfig config) {
        UUID gameId = UUID.randomUUID();
        
        // Factory nova para criar uma instância limpa do Service para cada jogo
        GameService newGameService = new GameService(config); 
        
        activeGames.put(gameId, newGameService);
        return gameId;
    }

    // Busca a partida para jogar
    public GameService getGameService(UUID gameId) {
        GameService service = activeGames.get(gameId);
        if (service == null) throw new IllegalArgumentException("Jogo não encontrado para o ID: " + gameId);
        return service;
    }
}
