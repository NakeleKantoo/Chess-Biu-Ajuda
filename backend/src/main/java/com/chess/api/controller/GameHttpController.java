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
import com.chess.api.dto.response.game.GameDTO;
import com.chess.api.dto.response.game.PendingGameDTO;
import com.chess.api.dto.response.game.SavedGameDTO;
import com.chess.api.mapper.GameApiMapper;
import com.chess.api.mapper.SavedGameApiMapper;
import com.chess.app.service.game.GameManagerService;
import com.chess.app.service.game.GamePersistenceService;
import com.chess.app.session.game.GameSessionManager;
import com.chess.domain.model.game.Game;
import com.chess.domain.model.game.GameConfig;
import com.chess.domain.model.game.PendingGame;
import com.chess.infrastructure.persistence.entity.GameEntity;
import com.chess.infrastructure.security.service.UserDetailsImpl;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/games")
public class GameHttpController {

    @Autowired private GameManagerService gameManagerService;
    @Autowired private GameSessionManager gameSessionManager;
    @Autowired private GamePersistenceService gamePersistenceService;

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

    @GetMapping("/{gameId}/players")
    public ResponseEntity<?> getPlayersInGame(@PathVariable UUID gameId) {
        var players = gameManagerService.getPlayersInGame(gameId);

        return ResponseEntity.ok(players);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGameSessionById(@PathVariable UUID gameId) {
        Game game = gameManagerService.getGameSessionById(gameId);

        return ResponseEntity.ok(GameApiMapper.toDTO(game, gameId));
    }

    @GetMapping("/pending/{gameId}")
    public ResponseEntity<PendingGameDTO> getPendingGameById(@PathVariable UUID gameId) {
        PendingGame pendingGame = gameManagerService.getPendingGameById(gameId);
        System.out.println(pendingGame.config());

        return ResponseEntity.ok(new PendingGameDTO(pendingGame));
    }

    @GetMapping("/saved/{gameId}")
    public ResponseEntity<SavedGameDTO> getGameEntityById(@PathVariable UUID gameId) {
        GameEntity game = gamePersistenceService.getGameById(gameId);

        return ResponseEntity.ok(SavedGameApiMapper.toDTO(game));
    }

}
