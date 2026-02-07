package com.chess.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record GameConfigDTO(

    @NotBlank(message = "O tipo de jogo é obrigatório")
    @Pattern(regexp = "STANDARD|CHESS960", message = "Tipo de jogo inválido (STANDARD, CHESS960)")
    String gameType,

    @NotBlank(message = "O controle de tempo é obrigatório")
    @Pattern(regexp = "BULLET|BLITZ|RAPID|CLASSICAL|CUSTOM", message = "Controle de tempo inválido")
    String timeControl,

    @NotBlank(message = "A preferência de cor é obrigatória")
    PlayerColorPreference playerColorPreference,

    @Positive(message = "O tempo para as brancas deve ser positivo")
    Long whiteTimeRemaining,

    @Positive(message = "O tempo para as pretas deve ser positivo")
    Long blackTimeRemaining,

    @PositiveOrZero(message = "O incremento de tempo deve ser zero ou positivo")
    Long incrementMillis
    
) {}