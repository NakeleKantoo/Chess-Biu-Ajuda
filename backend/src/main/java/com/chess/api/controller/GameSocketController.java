package com.chess.api.controller;

import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.chess.api.dto.request.game.MoveDTO;
import com.chess.api.mapper.GameApiMapper;
import com.chess.app.service.game.GameManagerService;
import com.chess.domain.exception.player.PlayerNotInGameException;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.game.Game;
import com.chess.domain.model.piece.Piece;
import com.chess.infrastructure.security.service.UserDetailsImpl;

import jakarta.validation.Valid;

@Controller
public class GameSocketController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private GameManagerService gameManagerService;
    
    @MessageMapping("/game/{gameId}/move")
    public void handleMove(
        @DestinationVariable UUID gameId,
        @Payload @Valid MoveDTO moveDTO,
        Principal principal // Alterado de @AuthenticationPrincipal UserDetailsImpl para Principal
    ) {

        // Verifica se há um usuário conectado na sessão WebSocket
        if (principal == null) {
            System.err.println("Principal is null");
            throw new PlayerNotInGameException(null);
        }

        // Extrai o UserDetailsImpl manualmente do Principal
        UserDetailsImpl user = null;
        if (principal instanceof Authentication auth) {
            user = (UserDetailsImpl) auth.getPrincipal();
        } else {
            // Fallback caso a autenticação venha de outra forma, mas geralmente é Authentication
            throw new PlayerNotInGameException(null);
        }

        Position from = Position.at(moveDTO.from());
        Position to = Position.at(moveDTO.to());
        Piece promotionPiece = moveDTO.promotion() != null ? Piece.create(moveDTO.promotion().charAt(0)) : null;

        Game game = gameManagerService.makeMove(from, to, promotionPiece, gameId, user.getId());

        messagingTemplate.convertAndSend("/topic/game/" + gameId, GameApiMapper.toDTO(game, gameId));
    }

    // Faça o mesmo ajuste para handleAction se necessário
    @MessageMapping("/game/{gameId}/{action}")
    public void handleAction(
        @DestinationVariable UUID gameId,
        @DestinationVariable String action,
        Principal principal
    ) {
        if (principal == null) {
            throw new PlayerNotInGameException(null);
        }
        
        UserDetailsImpl user = (UserDetailsImpl) ((Authentication) principal).getPrincipal();
        
        Game game = gameManagerService.performAction(action, gameId, user.getId());

        messagingTemplate.convertAndSend("/topic/game/" + gameId, GameApiMapper.toDTO(game, gameId));
    }

}