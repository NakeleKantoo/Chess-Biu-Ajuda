package com.chess.app.service.game;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chess.app.service.user.UserService;
import com.chess.app.session.GameSession;
import com.chess.domain.event.GameFinishedEvent;
import com.chess.domain.exception.game.DuplicateGameException;
import com.chess.domain.exception.game.GameNotFoundException;
import com.chess.domain.exception.game.InvalidGameStateException;
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

    private final GameRepository gameRepository;
    private final UserService userService;
    private final GamePersistenceMapper gamePersistenceMapper;

    @Transactional
    public GameEntity saveCompletedGame(Game game, UUID whitePlayerId, UUID blackPlayerId, UUID id) {
        if (game.getGameState() == GameState.ACTIVE) {
            throw new InvalidGameStateException("O jogo ainda está ativo e não pode ser salvo como concluído.", id, game.getGameState());
        }
        if (gameRepository.existsById(id)) {
            throw new DuplicateGameException(id);
        }

        UserEntity white = userService.getUserEntityById(whitePlayerId);
        UserEntity black = userService.getUserEntityById(blackPlayerId);

        GameEntity entity = gamePersistenceMapper.toEntity(game, white, black, id);
        GameEntity savedEntity = gameRepository.save(entity);

        return savedEntity;
    }

    public GameEntity getGameById(UUID id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public boolean existsById(UUID id) {
        return gameRepository.existsById(id);
    }

    @EventListener
    public void handleGameFinished(GameFinishedEvent event) {
        GameSession session = event.getGameSession();
        this.saveCompletedGame(
            session.getGame(),
            session.getWhitePlayerId(),
            session.getBlackPlayerId(),
            session.getSessionId()
        );
    }

}