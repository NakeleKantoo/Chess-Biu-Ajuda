package com.chess.app.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.chess.app.service.game.GameNotificationService;
import com.chess.app.service.game.GamePersistenceService;
import com.chess.app.session.game.GameSession;
import com.chess.domain.event.GameAutoStartEvent;
import com.chess.domain.event.GameFinishedEvent;

@Component
public class GameEventListener {

    @Autowired private GamePersistenceService gamePersistenceService;
    @Autowired private GameNotificationService gameNotificationService;

    @EventListener
    public void handleGameFinished(GameFinishedEvent event) {
        GameSession session = event.getGameSession();
        gamePersistenceService.saveCompletedGame(
            session.getGame(),
            session.getWhitePlayerId(),
            session.getBlackPlayerId(),
            session.getSessionId()
        );
        gameNotificationService.notifyGameUpdate(session.getGame(), session.getSessionId());
    }

    @EventListener
    public void handleGameAutoStart(GameAutoStartEvent event) {
        GameSession session = event.getGameSession();
        gameNotificationService.notifyGameUpdate(session.getGame(), session.getSessionId());
    }

}
