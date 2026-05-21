package com.chess.app.service.game;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.chess.api.dto.response.game.GameDTO;
import com.chess.api.mapper.GameApiMapper;
import com.chess.domain.model.game.Game;

@Service
public class GameNotificationService {
    @Autowired private SimpMessagingTemplate messagingTemplate;

    public void notifyGameUpdate(Game game, UUID gameId) {
        GameDTO dto = GameApiMapper.toDTO(game, gameId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, dto);
    }

    public void notifyDrawOffer(String opponentUsername) {
    messagingTemplate.convertAndSendToUser(
        opponentUsername, 
        "/queue/draw-offers", 
        "Oferta de empate recebida!"
    );
}
}
