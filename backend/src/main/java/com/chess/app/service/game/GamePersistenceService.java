package com.chess.app.service.game;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chess.app.service.user.UserService;
import com.chess.domain.exception.game.DuplicateGameException;
import com.chess.domain.exception.game.GameNotFoundException;
import com.chess.domain.model.game.Game;
import com.chess.domain.model.game.GameState;
import com.chess.infrastructure.persistence.entity.GameEntity;
import com.chess.infrastructure.persistence.entity.UserEntity;
import com.chess.infrastructure.persistence.mapper.GamePersistenceMapper;
import com.chess.infrastructure.repository.GameRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GamePersistenceService {

    private static final Logger log = LoggerFactory.getLogger(GamePersistenceService.class);

    private final GameRepository gameRepository;
    private final UserService userService;
    private final GamePersistenceMapper gamePersistenceMapper;

    @Transactional
    public GameEntity saveCompletedGame(Game game, UUID whitePlayerId, UUID blackPlayerId, UUID id) {
        if (game.getGameState() == GameState.ACTIVE) {
            throw new IllegalStateException("O jogo ainda está ativo e não pode ser salvo como concluído.");
        }
        if (gameRepository.existsById(id)) {
            throw new DuplicateGameException(id);
        }

        log.info("Salvando jogo {} (white: {}, black: {})", id, whitePlayerId, blackPlayerId);

        UserEntity white = userService.getUserEntityById(whitePlayerId);
        UserEntity black = userService.getUserEntityById(blackPlayerId);

        GameEntity entity = gamePersistenceMapper.toEntity(game, white, black, id);
        GameEntity savedEntity = gameRepository.save(entity);

        log.info("Jogo {} salvo com sucesso", id);

        return savedEntity;
    }

    public GameEntity getGameById(UUID id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public boolean existsById(UUID id) {
        return gameRepository.existsById(id);
    }

}