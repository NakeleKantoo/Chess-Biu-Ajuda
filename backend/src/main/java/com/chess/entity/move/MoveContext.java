package com.chess.entity.move;

import java.util.ArrayList;
import java.util.List;

import com.chess.entity.board.Board;

public class MoveContext {
    private final List<MoveBuilder> moveBuilders;
    private final List<Board> resultBoards;

    public MoveContext() {
        this.moveBuilders = new ArrayList<>();
        this.resultBoards = new ArrayList<>();
    }

    public void addMove(MoveBuilder moveBuilder, Board resultBoard) {
        this.moveBuilders.add(moveBuilder);
        this.resultBoards.add(resultBoard);
    }

    public int getMoveCount() {
        return moveBuilders.size();
    }

    public MoveBuilder getMoveBuilder(int index) {
        return moveBuilders.get(index);
    }

    public Board getResultBoard(int index) {
        return resultBoards.get(index);
    }
}
