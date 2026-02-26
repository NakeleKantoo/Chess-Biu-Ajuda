package com.chess.api.dto.response.game.base;

import java.util.List;
import java.util.Map;

public record BoardDTO(
    String fen,
    Map<String, List<String>> legalMoves,
    MoveResponse lastMove
) {}
