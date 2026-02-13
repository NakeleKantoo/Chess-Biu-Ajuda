package com.chess.app.service.game;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.chess.app.session.GameSession;
import com.chess.app.session.GameSessionManager;
import com.chess.domain.exception.game.InvalidGameAction;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.game.Game;
import com.chess.domain.model.piece.Piece;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GameManagerService {

    private final GameSessionManager sessionManager;

    public Game getGameById(UUID gameId) {
        return sessionManager.getGameSession(gameId).getGame();
    }

    public Game makeMove(Position from, Position to, Piece promotionPiece, UUID gameId, UUID userId) {
        GameSession session = sessionManager.getGameSession(gameId);

        return session.makeMove(from, to, promotionPiece, userId);
    }

    public Game performAction(String action, UUID gameId, UUID userId) {
        GameSession session = sessionManager.getGameSession(gameId);

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
                throw new InvalidGameAction(action);
        }

        return game;
    }

}
