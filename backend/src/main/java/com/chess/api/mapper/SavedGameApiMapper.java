package com.chess.api.mapper;

import java.util.List;

import com.chess.api.dto.response.game.MoveDTO;
import com.chess.api.dto.response.game.SavedGameDTO;
import com.chess.domain.model.game.GameConfig.GameType;
import com.chess.infrastructure.persistence.entity.GameEntity;
import com.chess.infrastructure.persistence.entity.MoveEntity;
import com.chess.domain.model.game.GameEndReason;
import com.chess.domain.model.game.GameState;

public class SavedGameApiMapper {
    
    public static SavedGameDTO toDTO(GameEntity entity) {
        List<MoveDTO> moves = mapMoves(entity.getMoves());
        String whitePlayerName = entity.getWhitePlayer().getUsername();
        String blackPlayerName = entity.getBlackPlayer().getUsername();
        GameState gameState = entity.getGameState();
        GameEndReason gameEndReason = entity.getGameEndReason();
        GameType gameType = entity.getGameType();
        Long whiteTimeRemaining = entity.getWhiteTimeRemaining();
        Long blackTimeRemaining = entity.getBlackTimeRemaining();
        String starterFen = entity.getStarterFen();

        return new SavedGameDTO(
            moves,
            whitePlayerName,
            blackPlayerName,
            gameState,
            gameEndReason,
            gameType,
            whiteTimeRemaining,
            blackTimeRemaining,
            starterFen
        );
    }

    private static List<MoveDTO> mapMoves(List<MoveEntity> moveEntities) {
        return moveEntities.stream()
            .map(SavedGameApiMapper::mapMove)
            .toList();
    }

    private static MoveDTO mapMove(MoveEntity moveEntity) {
        return new MoveDTO(
            moveEntity.getUci(),
            moveEntity.getSan()
        );
    }

}
