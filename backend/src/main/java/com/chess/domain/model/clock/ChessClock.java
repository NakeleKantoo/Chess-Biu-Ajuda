package com.chess.domain.model.clock;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import com.chess.domain.model.base.Color;

public class ChessClock {
    
    // Tempo restante em milissegundos para cada cor.
    private Map<Color, Long> timeRemaining;
    // Incremento de tempo por movimento (em milissegundos).
    private long incrementMillis;

    // Timestamp do último movimento para cálculo do tempo decorrido.
    private long lastMoveTimestamp;
    // Cor atual.
    private Color currentTurn;
    // Indica se o relógio está em execução.
    private boolean isRunning;

    protected ChessClock(long whiteTimeRemaining, long blackTimeRemaining, long incrementMillis, Color currentTurn) {
        this.timeRemaining = new EnumMap<>(Color.class);
        this.timeRemaining.put(Color.WHITE, whiteTimeRemaining);
        this.timeRemaining.put(Color.BLACK, blackTimeRemaining);

        this.incrementMillis = incrementMillis;
        this.currentTurn = currentTurn;
        this.isRunning = false;
    }

    protected ChessClock(long initialTimeMillis, Color currentTurn) {
        this(initialTimeMillis, initialTimeMillis, 0, currentTurn);
    }

    protected ChessClock(long initialTimeMillis, long incrementMillis, Color currentTurn) {
        this(initialTimeMillis, initialTimeMillis, incrementMillis, currentTurn);
    }

    public long getLastMoveTimestamp() { return lastMoveTimestamp; }
    public Color getCurrentTurn() { return currentTurn; }
    public boolean isRunning() { return isRunning; }

    public long getTimeRemaining(Color color) {
        Objects.requireNonNull(color, "A cor não pode ser nula.");

        long timeRemaining = this.timeRemaining.get(color);

        if (isRunning && color == currentTurn) {
            long timeElapsed = System.currentTimeMillis() - lastMoveTimestamp;
            return Math.max(0, timeRemaining - timeElapsed);
        }

        return timeRemaining;
    }

    public void start() {
        if (isRunning) return;

        boolean anyLoss = timeRemaining.values().stream().anyMatch(time -> time <= 0);

        if (anyLoss) {
            return;
        }

        this.isRunning = true;
        this.lastMoveTimestamp = System.currentTimeMillis();
    }

    public void stop() {
        if (!isRunning) return;

        updateTime(0);
        this.isRunning = false;
    }

    public long makeMove() {
        if (!isRunning) throw new IllegalStateException("O relógio deve estar em execução para fazer um movimento.");

        long timeElapsed = updateTime(this.incrementMillis);
        changeTurn();

        return timeElapsed;
    }

    private long updateTime(long incrementMillis) {
        long timeElapsed = System.currentTimeMillis() - lastMoveTimestamp;
        long currentTime = timeRemaining.get(currentTurn);

        long updatedTime = currentTime - timeElapsed;

        // Proteção contra negativo
        if (updatedTime < 0) {
            updatedTime = 0;
        } else {
            updatedTime += incrementMillis;
        }

        timeRemaining.put(currentTurn, updatedTime);

        return timeElapsed;
    }

    private void changeTurn() {
        currentTurn =  currentTurn.opposite();
        lastMoveTimestamp = System.currentTimeMillis();
    }

}
