package com.chess.domain.event;

import org.springframework.context.ApplicationEvent;

import com.chess.app.session.game.GameSession;

import lombok.Getter;

@Getter
public class GameAutoStartEvent extends ApplicationEvent {
    
    private final GameSession gameSession;

    public GameAutoStartEvent(GameSession gameSession) {
        super(gameSession);
        this.gameSession = gameSession;
    }

}
