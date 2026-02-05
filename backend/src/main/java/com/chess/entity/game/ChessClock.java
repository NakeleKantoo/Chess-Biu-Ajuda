package com.chess.entity.game;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import com.chess.entity.base.Color;

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

    public ChessClock(long whiteTimeRemaining, long blackTimeRemaining, long incrementMillis, Color currentTurn) {
        this.timeRemaining = new EnumMap<>(Color.class);

        this.timeRemaining.put(Color.WHITE, whiteTimeRemaining);
        this.timeRemaining.put(Color.BLACK, blackTimeRemaining);

        this.incrementMillis = incrementMillis;
        this.currentTurn = currentTurn;
        this.isRunning = false;
    }

    public ChessClock(long initialTimeMillis, Color currentTurn) {
        this(initialTimeMillis, initialTimeMillis, 0, currentTurn);
    }

    public ChessClock(long initialTimeMillis, long incrementMillis, Color currentTurn) {
        this(initialTimeMillis, initialTimeMillis, incrementMillis, currentTurn);
    }

    public Color getCurrentTurn() { return currentTurn; }

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

        updateTime();
        this.isRunning = false;
    }

    public void makeMove() {
        if (!isRunning) throw new IllegalStateException("O relógio deve estar em execução para fazer um movimento.");

        updateTime(this.incrementMillis);
        changeTurn();
    }

    private void updateTime() {
        updateTime(0);
    }

    private void updateTime(long incrementMillis) {
        long timeElapsed = System.currentTimeMillis() - lastMoveTimestamp;
        long currentTime = timeRemaining.get(currentTurn);

        long updatedTime = currentTime - timeElapsed;

        if (updatedTime < 0) {
            updatedTime = 0;
        } else {
            updatedTime += incrementMillis;
        }

        timeRemaining.put(
            currentTurn,
            updatedTime
        );
    }

    private void changeTurn() {
        currentTurn =  currentTurn.opposite();
        lastMoveTimestamp = System.currentTimeMillis();
    }

}
