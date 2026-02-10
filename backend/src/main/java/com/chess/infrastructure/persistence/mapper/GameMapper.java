package com.chess.infrastructure.persistence.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chess.domain.model.base.Color;
import com.chess.domain.model.board.Board;
import com.chess.domain.model.game.Game;
import com.chess.domain.model.move.Move;
import com.chess.infrastructure.persistence.entity.GameEntity;
import com.chess.infrastructure.persistence.entity.MoveEntity;
import com.chess.infrastructure.persistence.entity.UserEntity;

@Component
public class GameMapper {

    @Autowired
    MoveMapper moveMapper;
    
    public GameEntity toEntity(Game game, UserEntity whitePlayer, UserEntity blackPlayer, UUID sessionId) {
        if (game == null) return null;

        GameEntity entity = new GameEntity();

        entity.setId(sessionId);
        entity.setWhitePlayer(whitePlayer);
        entity.setBlackPlayer(blackPlayer);
        entity.setMoves(toMoveEntities(game, entity));
        entity.setGameState(game.getGameState());
        entity.setGameEndReason(game.getGameEndReason());
        entity.setGameType(game.getGameType());
        entity.setWhiteTimeRemaining(game.getTimeRemaining(Color.WHITE));
        entity.setBlackTimeRemaining(game.getTimeRemaining(Color.BLACK));
        entity.setStarterFen(game.getBoardHistory().get(0).toFen());
        
        return entity;
    }

    private List<MoveEntity> toMoveEntities(Game game, GameEntity gameEntity) {
        List<MoveEntity> moveEntities = new ArrayList<>();
        List<Board> boardHistory = game.getBoardHistory();
        List<Move> moves = game.getMoveHistory();

        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            Board board = boardHistory.get(i + 1);

            MoveEntity moveEntity = moveMapper.toEntity(gameEntity, move, board, board.getFullMoveClock());
            moveEntities.add(moveEntity);
        }

        return moveEntities;
    }

}
