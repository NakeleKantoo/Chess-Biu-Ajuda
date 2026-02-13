package com.chess.app.session.player;

import java.util.UUID;

public record PlayersIDs(
    UUID white,
    UUID black
) {
    public boolean isPlayer(UUID id) {
        return white.equals(id) || black.equals(id);
    }
}
