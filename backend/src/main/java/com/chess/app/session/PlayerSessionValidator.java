package com.chess.app.session;

import java.util.Objects;
import java.util.UUID;

import com.chess.domain.exception.player.NotPlayerTurnException;
import com.chess.domain.exception.player.PlayerNotInGameException;
import com.chess.domain.model.base.Color;

public class PlayerSessionValidator {

    private PlayerSessionValidator() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    public static void validatePlayerInSession(UUID userId, PlayersIDs playersIDs) {
        Objects.requireNonNull(userId, "O ID do usuário não pode ser nulo.");
        Objects.requireNonNull(playersIDs, "Os IDs dos jogadores não podem ser nulos.");

        if (!playersIDs.isPlayer(userId)) {
            throw new PlayerNotInGameException(userId);
        }
    }
    
    public static void validatePlayerTurn(UUID userId, UUID currentPlayerId, PlayersIDs playersIDs) {
        Objects.requireNonNull(userId, "O ID do usuário não pode ser nulo.");
        Objects.requireNonNull(currentPlayerId, "O ID do jogador atual não pode ser nulo.");
        Objects.requireNonNull(playersIDs, "Os IDs dos jogadores não podem ser nulos.");

        validatePlayerInSession(userId, playersIDs);

        if (!userId.equals(currentPlayerId)) {
            throw new NotPlayerTurnException(userId);
        }
    }

    public static Color getPlayerColor(UUID userId, PlayersIDs playersIDs) {
        Objects.requireNonNull(userId, "O ID do usuário não pode ser nulo.");
        Objects.requireNonNull(playersIDs, "Os IDs dos jogadores não podem ser nulos.");

        validatePlayerInSession(userId, playersIDs);

        if (userId.equals(playersIDs.white())) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    public static UUID getOpponentId(UUID userId, PlayersIDs playersIDs) {
        Objects.requireNonNull(userId, "O ID do usuário não pode ser nulo.");
        Objects.requireNonNull(playersIDs, "Os IDs dos jogadores não podem ser nulos.");

        validatePlayerInSession(userId, playersIDs);

        if (userId.equals(playersIDs.white())) {
            return playersIDs.black();
        } else {
            return playersIDs.white();
        } 
    }

    public static UUID getCurrentPlayerId(Color currentPlayerColor, PlayersIDs playersIDs) {
        Objects.requireNonNull(currentPlayerColor, "A cor do jogador atual não pode ser nula.");
        Objects.requireNonNull(playersIDs, "Os IDs dos jogadores não podem ser nulos.");

        UUID currentPlayerId = currentPlayerColor.isWhite() ? playersIDs.white() : playersIDs.black();
        
        return currentPlayerId;
    }

}
