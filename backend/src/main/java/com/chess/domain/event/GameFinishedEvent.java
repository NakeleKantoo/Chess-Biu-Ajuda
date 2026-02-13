package com.chess.domain.event;

import org.springframework.context.ApplicationEvent;

import com.chess.app.session.game.GameSession;

import lombok.Getter;

@Getter
public class GameFinishedEvent extends ApplicationEvent {
    
    private final GameSession gameSession;

    public GameFinishedEvent(GameSession gameSession) {
        super(gameSession);
        this.gameSession = gameSession;
    }

}
