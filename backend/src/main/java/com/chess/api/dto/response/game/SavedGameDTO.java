package com.chess.api.dto.response.game;

import java.util.List;

import com.chess.api.dto.response.game.base.MoveResponse;
import com.chess.domain.model.game.GameEndReason;
import com.chess.domain.model.game.GameState;
import com.chess.domain.model.game.GameConfig.GameType;

public record SavedGameDTO(
    List<MoveResponse> moves,
    String whitePlayerName,
    String blackPlayerName,
    GameState gameState,
    GameEndReason gameEndReason,
    GameType gameType,
    Long whiteTimeRemaining,
    Long blackTimeRemaining,
    String starterFen
) {}
