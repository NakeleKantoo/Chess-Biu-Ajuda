package com.chess.api.dto.request.game;

import jakarta.validation.constraints.NotBlank;

public record MoveRequest(
    @NotBlank(message = "A posição de origem é obrigatória")
    String from,
    @NotBlank(message = "A posição de destino é obrigatória")
    String to,
    String promotion
) {}
