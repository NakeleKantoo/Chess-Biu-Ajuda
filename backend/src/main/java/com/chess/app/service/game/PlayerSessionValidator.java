package com.chess.app.service.game;

import java.util.Objects;
import java.util.UUID;

import com.chess.domain.exception.player.PlayerNotInGameException;
import com.chess.domain.model.base.Color;

public class PlayerSessionValidator {

    private PlayerSessionValidator() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }
    
    public static void validatePlayerTurn(UUID playerId, UUID currentPlayerId) {
        Objects.requireNonNull(playerId, "O ID do jogador não pode ser nulo.");
        Objects.requireNonNull(currentPlayerId, "O ID do jogador atual não pode ser nulo.");

        if (!playerId.equals(currentPlayerId)) {
            throw new IllegalStateException("Não é a vez do jogador com ID: " + playerId);
        }
    }

    public static Color getPlayerColor(UUID playerId, UUID whitePlayerId, UUID blackPlayerId) {
        Objects.requireNonNull(playerId, "O ID do jogador não pode ser nulo.");
        Objects.requireNonNull(whitePlayerId, "O ID do jogador branco não pode ser nulo.");
        Objects.requireNonNull(blackPlayerId, "O ID do jogador preto não pode ser nulo.");

        if (playerId.equals(whitePlayerId)) {
            return Color.WHITE;
        } else if (playerId.equals(blackPlayerId)) {
            return Color.BLACK;
        } else {
            throw new PlayerNotInGameException(playerId);
        }
    }

    public static UUID getOpponentId(UUID playerId, UUID whitePlayerId, UUID blackPlayerId) {
        Objects.requireNonNull(playerId, "O ID do jogador não pode ser nulo.");
        Objects.requireNonNull(whitePlayerId, "O ID do jogador branco não pode ser nulo.");
        Objects.requireNonNull(blackPlayerId, "O ID do jogador preto não pode ser nulo.");

        if (playerId.equals(whitePlayerId)) {
            return blackPlayerId;
        } else if (playerId.equals(blackPlayerId)) {
            return whitePlayerId;
        } else {
            throw new PlayerNotInGameException(playerId);
        }
    }

    public static UUID getCurrentPlayerId(Color currentPlayerColor, UUID whitePlayerId, UUID blackPlayerId) {
        Objects.requireNonNull(currentPlayerColor, "A cor do jogador atual não pode ser nula.");
        Objects.requireNonNull(whitePlayerId, "O ID do jogador branco não pode ser nulo.");
        Objects.requireNonNull(blackPlayerId, "O ID do jogador preto não pode ser nulo.");

        UUID currentPlayerId = currentPlayerColor.isWhite() ? whitePlayerId : blackPlayerId;
        
        return currentPlayerId;
    }

}
