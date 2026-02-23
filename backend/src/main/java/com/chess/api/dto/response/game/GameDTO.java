package com.chess.api.dto.response.game;

import java.util.List;
import java.util.UUID;

import com.chess.api.dto.response.game.base.BoardDTO;
import com.chess.api.dto.response.game.base.ClockDTO;
import com.chess.api.dto.response.game.base.MoveDTO;
import com.chess.domain.model.game.GameEndReason;
import com.chess.domain.model.game.GameState;

public record GameDTO(
    UUID id,

    BoardDTO board,
    GameState state,
    GameEndReason endReason,

    ClockDTO clock,
    List<MoveDTO> moveHistory
) {}
