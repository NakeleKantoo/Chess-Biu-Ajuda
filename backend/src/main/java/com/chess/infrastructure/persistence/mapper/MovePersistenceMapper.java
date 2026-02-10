package com.chess.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.chess.domain.model.board.Board;
import com.chess.domain.model.move.Move;
import com.chess.infrastructure.persistence.entity.GameEntity;
import com.chess.infrastructure.persistence.entity.MoveEntity;

@Component
public class MovePersistenceMapper {
    
    public MoveEntity toEntity(GameEntity gameEntity, Move move, Board board, int moveNumber) {
        if (move == null) return null;

        MoveEntity entity = new MoveEntity();

        entity.setGame(gameEntity);
        entity.setMoveNumber(moveNumber);
        entity.setUci(move.getUci());
        entity.setSan(move.getSan());
        entity.setFenSnapshot(board.toFen());

        return entity;
    }

}