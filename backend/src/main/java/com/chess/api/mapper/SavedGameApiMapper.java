package com.chess.api.mapper;

import java.util.List;

import com.chess.api.dto.response.game.SavedGameDTO;
import com.chess.api.dto.response.game.base.MoveResponse;
import com.chess.domain.model.game.GameConfig.GameType;
import com.chess.infrastructure.persistence.entity.GameEntity;
import com.chess.infrastructure.persistence.entity.MoveEntity;
import com.chess.domain.model.game.GameEndReason;
import com.chess.domain.model.game.GameState;

public class SavedGameApiMapper {
    
    public static SavedGameDTO toDTO(GameEntity entity) {
        List<MoveResponse> moves = mapMoves(entity.getMoves());
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

    private static List<MoveResponse> mapMoves(List<MoveEntity> moveEntities) {
        return moveEntities.stream()
            .map(SavedGameApiMapper::mapMove)
            .toList();
    }

    private static MoveResponse mapMove(MoveEntity moveEntity) {
        return new MoveResponse(
            moveEntity.getUci(),
            moveEntity.getSan()
        );
    }

}
