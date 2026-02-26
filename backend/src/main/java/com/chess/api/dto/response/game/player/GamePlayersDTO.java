package com.chess.api.dto.response.game.player;

public record GamePlayersDTO(
    PlayerDTO whitePlayer,
    PlayerDTO blackPlayer
) {}
