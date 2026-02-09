package com.chess.api.dto.request.game;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MoveDTO(

    @NotBlank(message = "A posição de origem é obrigatória")
    @Pattern(regexp = "^[a-h][1-8]$", message = "Posição de origem inválida (ex: e2)")
    String from,

    @NotBlank(message = "A posição de destino é obrigatória")
    @Pattern(regexp = "^[a-h][1-8]$", message = "Posição de destino inválida (ex: e4)")
    String to,

    @Pattern(regexp = "^[QRNBqrnb]$", message = "Promoção inválida (Q, R, N, B)")
    String promotion

) {}
