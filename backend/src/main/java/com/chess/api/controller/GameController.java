package com.chess.api.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chess.api.dto.request.game.GameConfigDTO;
import com.chess.api.dto.request.game.JoinGameRequest;
import com.chess.api.dto.request.game.MoveDTO;
import com.chess.api.dto.response.game.GameDTO;
import com.chess.api.dto.response.game.PendingGameDTO;
import com.chess.api.dto.response.game.SavedGameDTO;
import com.chess.api.mapper.GameApiMapper;
import com.chess.api.mapper.SavedGameApiMapper;
import com.chess.app.service.game.GameManagerService;
import com.chess.app.service.game.GamePersistenceService;
import com.chess.app.session.GameSession;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.game.GameConfig;
import com.chess.domain.model.piece.Piece;
import com.chess.infrastructure.security.service.UserDetailsImpl;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/games")
public class GameController {
    
    @Autowired GameManagerService gameManager;
    @Autowired GamePersistenceService gamePersistenceService;

    @PostMapping
    public ResponseEntity<PendingGameDTO> createGame(
        @RequestBody @Valid GameConfigDTO configDTO,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {

        GameConfig config = GameApiMapper.fromGameConfigDTO(configDTO);
        PendingGameDTO pendingGameDTO = gameManager.createGame(config, user.getId());

        return ResponseEntity.ok(pendingGameDTO);
    }

    @PostMapping("/join")
    public ResponseEntity<UUID> joinGame(
        @RequestBody JoinGameRequest joinGameDTO,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {

        UUID gameId = gameManager.joinGame(joinGameDTO.gameCode(), user.getId());
        URI location = URI.create(String.format("/api/games/%s", gameId));

        return ResponseEntity.created(location).body(gameId);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable UUID gameId) {
        return ResponseEntity.ok(GameApiMapper.toDTO(gameManager.getGameSession(gameId).getGame(), gameId));
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<GameDTO> makeMove(
        @PathVariable UUID gameId,
        @RequestBody @Valid MoveDTO moveDTO,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {
        GameSession gameSession = gameManager.getGameSession(gameId);
        Position from = Position.at(moveDTO.from());
        Position to = Position.at(moveDTO.to());
        Piece promotionPiece = moveDTO.promotion() != null ? Piece.create(moveDTO.promotion().charAt(0)) : null;

        gameSession.makeMove(from, to, promotionPiece, user.getId());

        return ResponseEntity.ok(GameApiMapper.toDTO(gameSession.getGame(), gameId));
    }

    @PostMapping("/{gameId}/{action}")
    public ResponseEntity<GameDTO> performAction(
        @PathVariable UUID gameId,
        @PathVariable String action,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {
        GameSession gameSession = gameManager.getGameSession(gameId);
        UUID playerId = user.getId();
        
        switch (action.toLowerCase()) {
            case "offer-draw":
                gameSession.offerDraw(playerId);
                break;
            case "resign":
                gameSession.resign(playerId);
                break;
            case "accept-draw":
                gameSession.acceptDraw(playerId);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(GameApiMapper.toDTO(gameSession.getGame(), gameId));
    }

    @GetMapping("/saved/{gameId}")
    public ResponseEntity<SavedGameDTO> getGameEntityById(@PathVariable UUID gameId) {
        return ResponseEntity.ok(SavedGameApiMapper.toDTO(gamePersistenceService.getGameById(gameId)));
    }
    

}
