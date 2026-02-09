package com.chess.service.game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.chess.dto.response.PendingGameDTO;
import com.chess.entity.base.Color;
import com.chess.entity.game.GameConfig;
import com.chess.entity.game.PendingGame;

@Service
public class GameManager {
    
    // Onde tudo fica guardado
    private final Map<UUID, GameService> activeGames = new ConcurrentHashMap<>();

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

        GameService gameService = new GameService(pendingGame.config(), whitePlayerId, blackPlayerId);
        activeGames.put(pendingGame.gameId(), gameService);

        return pendingGame.gameId();
    }

    // Busca a partida para jogar
    public GameService getGameService(UUID gameId) {
        GameService service = activeGames.get(gameId);
        if (service == null) throw new IllegalArgumentException("Jogo não encontrado para o ID: " + gameId);
        return service;
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

}
