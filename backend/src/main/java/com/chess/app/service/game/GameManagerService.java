package com.chess.app.service.game;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.chess.api.dto.response.game.player.GamePlayersDTO;
import com.chess.api.dto.response.game.player.PlayerDTO;
import com.chess.app.service.user.UserService;
import com.chess.app.session.game.GameSession;
import com.chess.app.session.game.GameSessionManager;
import com.chess.domain.exception.game.InvalidGameActionException;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.game.Game;
import com.chess.domain.model.game.PendingGame;
import com.chess.domain.model.piece.Piece;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GameManagerService {

    private final GameSessionManager sessionManager;
    private final UserService userService;

    public GamePlayersDTO getPlayersInGame(UUID gameId) {
        GameSession game = sessionManager.getGameSessionById(gameId);
        PlayerDTO whitePlayer = new PlayerDTO(game.getWhitePlayerId(), userService.getUsernameById(game.getWhitePlayerId()));
        PlayerDTO blackPlayer = new PlayerDTO(game.getBlackPlayerId(), userService.getUsernameById(game.getBlackPlayerId()));
        return new GamePlayersDTO(whitePlayer, blackPlayer);
    }

    public Game getGameSessionById(UUID gameId) {
        return sessionManager.getGameSessionById(gameId).getGame();
    }

    public PendingGame getPendingGameById(UUID gameId) {
        return sessionManager.getPendingGameById(gameId);
    }

    public Game makeMove(Position from, Position to, Piece promotionPiece, UUID gameId, UUID userId) {
        GameSession session = sessionManager.getGameSessionById(gameId);

        return session.makeMove(from, to, promotionPiece, userId);
    }

    public Game performAction(String action, UUID gameId, UUID userId) {
        GameSession session = sessionManager.getGameSessionById(gameId);

        Game game;
        switch (action.toLowerCase()) {
            case "offer-draw":
                game = session.offerDraw(userId);
                break;
            case "resign":
                game = session.resign(userId);
                break;
            case "accept-draw":
                game = session.acceptDraw(userId);
                break;
            default:
                throw new InvalidGameActionException(action);
        }

        return game;
    }

}
