package com.chess.api.dto.request.game;

import com.chess.domain.model.game.GameConfig.GameType;
import com.chess.domain.model.game.GameConfig.TimeControl;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record GameConfigDTO(

    @NotNull(message = "O tipo de jogo é obrigatório")
    GameType gameType,

    @NotNull(message = "O controle de tempo é obrigatório")
    TimeControl timeControl,

    @NotNull(message = "A preferência de cor é obrigatória")
    PlayerColorPreference playerColorPreference,

    @Positive(message = "O tempo para as brancas deve ser positivo")
    Long whiteTimeRemaining,

    @Positive(message = "O tempo para as pretas deve ser positivo")
    Long blackTimeRemaining,

    @PositiveOrZero(message = "O incremento de tempo deve ser zero ou positivo")
    Long incrementMillis
    
) {}