package com.chess.api.dto.request.game;

import jakarta.validation.constraints.NotBlank;

public record JoinGameRequest(
    @NotBlank(message = "O código do jogo é obrigatório")
    String gameCode
) {}