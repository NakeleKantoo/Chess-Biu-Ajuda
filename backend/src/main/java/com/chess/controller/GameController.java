package com.chess.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chess.dto.request.GameConfigDTO;
import com.chess.dto.request.MoveDTO;
import com.chess.dto.response.GameResponseDTO;
import com.chess.entity.base.Color;
import com.chess.entity.base.Position;
import com.chess.entity.game.GameConfig;
import com.chess.entity.piece.Piece;
import com.chess.mapper.GameMapper;
import com.chess.service.game.GameManager;
import com.chess.service.game.GameService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/games")
public class GameController {
    
    @Autowired
    GameManager gameManager;

    @PostMapping
    public ResponseEntity<GameResponseDTO> createGame(@RequestBody @Valid GameConfigDTO configDTO) {
        GameConfig config = GameMapper.fromGameConfigDTO(configDTO);
        UUID gameId = gameManager.createGame(config);

        URI location = URI.create(String.format("/api/games/%s", gameId)); 

        return ResponseEntity
                .created(location)
                .body(GameMapper.toDTO(gameManager.getGameService(gameId).getGame(), gameId));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponseDTO> getGameById(@PathVariable UUID gameId) {
        return ResponseEntity.ok(GameMapper.toDTO(gameManager.getGameService(gameId).getGame(), gameId));
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<GameResponseDTO> makeMove(@PathVariable UUID gameId, @RequestBody @Valid MoveDTO moveDTO) {
        GameService gameService = gameManager.getGameService(gameId);
        Position from = Position.at(moveDTO.from());
        Position to = Position.at(moveDTO.to());
        Piece promotionPiece = moveDTO.promotion() != null ? Piece.create(moveDTO.promotion().charAt(0)) : null;
        gameService.makeMove(from, to, promotionPiece);
        return ResponseEntity.ok(GameMapper.toDTO(gameService.getGame(), gameId));
    }

    @PostMapping("/{gameId}/{action}")
    public ResponseEntity<GameResponseDTO> performAction(@PathVariable UUID gameId, @PathVariable String action) {
        GameService gameService = gameManager.getGameService(gameId);
        Color currentPlayer = gameService.getCurrentPlayer(); // TODO: trocar pelo usuário autenticado quando implementado
        switch (action.toLowerCase()) {
            case "offer-draw":
                gameService.offerDraw(currentPlayer);
                break;
            case "resign":
                gameService.resign(currentPlayer);
                break;
            case "accept-draw":
                gameService.acceptDraw(currentPlayer);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(GameMapper.toDTO(gameService.getGame(), gameId));
    }

}
