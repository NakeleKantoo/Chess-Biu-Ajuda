package com.chess.api.dto.response.game.base;

import java.util.List;
import java.util.Map;

import com.chess.domain.model.board.BoardState;

public record BoardDTO(
    String fen,
    Map<String, List<String>> legalMoves,
    MoveResponse lastMove,
    BoardState boardState
) {}
