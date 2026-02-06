package com.chess.dto.response;

import java.util.List;
import java.util.UUID;

import com.chess.entity.game.GameEndReason;
import com.chess.entity.game.GameState;

public record GameResponseDTO(
    UUID id,

    BoardDTO board,
    GameState state,
    GameEndReason endReason,

    ClockDTO clock,
    List<MoveDTO> moveHistory
) {}
