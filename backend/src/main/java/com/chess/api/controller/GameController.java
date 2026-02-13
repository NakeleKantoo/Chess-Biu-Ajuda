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
import com.chess.app.session.GameSessionManager;
import com.chess.domain.model.base.Position;
import com.chess.domain.model.game.Game;
import com.chess.domain.model.game.GameConfig;
import com.chess.domain.model.game.PendingGame;
import com.chess.domain.model.piece.Piece;
import com.chess.infrastructure.persistence.entity.GameEntity;
import com.chess.infrastructure.security.service.UserDetailsImpl;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/games")
public class GameController {
    
    @Autowired GameManagerService gameManagerService;
    @Autowired GameSessionManager gameSessionManager;
    @Autowired GamePersistenceService gamePersistenceService;

    @PostMapping
    public ResponseEntity<PendingGameDTO> createGame(
        @RequestBody @Valid GameConfigDTO configDTO,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {

        GameConfig config = GameApiMapper.fromGameConfigDTO(configDTO);
        PendingGame pendingGame = gameSessionManager.createGame(config, user.getId());

        return ResponseEntity.ok(new PendingGameDTO(pendingGame));
    }

    @PostMapping("/join")
    public ResponseEntity<PendingGameDTO> joinGame(
        @RequestBody JoinGameRequest joinGameDTO,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {

        PendingGame pendingGame = gameSessionManager.joinGame(joinGameDTO.gameCode(), user.getId());
        URI location = URI.create(String.format("/api/games/%s", pendingGame.gameId()));

        return ResponseEntity.created(location).body(new PendingGameDTO(pendingGame));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable UUID gameId) {
        Game game = gameManagerService.getGameById(gameId);

        return ResponseEntity.ok(GameApiMapper.toDTO(game, gameId));
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<GameDTO> makeMove(
        @PathVariable UUID gameId,
        @RequestBody @Valid MoveDTO moveDTO,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {
        Position from = Position.at(moveDTO.from());
        Position to = Position.at(moveDTO.to());
        Piece promotionPiece = moveDTO.promotion() != null ? Piece.create(moveDTO.promotion().charAt(0)) : null;

        Game game = gameManagerService.makeMove(from, to, promotionPiece, gameId);

        return ResponseEntity.ok(GameApiMapper.toDTO(game, gameId));
    }

    @PostMapping("/{gameId}/{action}")
    public ResponseEntity<GameDTO> performAction(
        @PathVariable UUID gameId,
        @PathVariable String action,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {
        Game game = gameManagerService.performAction(action, gameId, user.getId());

        return ResponseEntity.ok(GameApiMapper.toDTO(game, gameId));
    }

    @GetMapping("/saved/{gameId}")
    public ResponseEntity<SavedGameDTO> getGameEntityById(@PathVariable UUID gameId) {
        GameEntity game = gamePersistenceService.getGameById(gameId);

        return ResponseEntity.ok(SavedGameApiMapper.toDTO(game));
    }
    

}
