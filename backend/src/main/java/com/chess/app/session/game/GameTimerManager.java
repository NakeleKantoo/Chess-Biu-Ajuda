package com.chess.app.session.game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class GameTimerManager {
    
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(8);
    private Map<UUID, ScheduledFuture<?>> autoStartTasks = new ConcurrentHashMap<>();
    private Map<UUID, ScheduledFuture<?>> timeoutTasks = new ConcurrentHashMap<>();


    public void scheduleAutoStart(UUID sessionId, Runnable callback, long delayMillis) {
        scheduleTask(sessionId, callback, delayMillis, autoStartTasks);
    }

    public void scheduleTimeout(UUID sessionId, Runnable callback, long timeoutMillis) {
        if (timeoutMillis <= 0) timeoutMillis = 1;
        scheduleTask(sessionId, callback, timeoutMillis, timeoutTasks);
    }

    public void cancelAutoStart(UUID sessionId) {
        cleanupTask(sessionId, autoStartTasks);
    }

    public void cancelTimeout(UUID sessionId) {
        cleanupTask(sessionId, timeoutTasks);
    }

    public void cancelAllTimers(UUID sessionId) {
        cancelAutoStart(sessionId);
        cancelTimeout(sessionId);
    }

    private void scheduleTask(UUID sessionId, Runnable callback, long delayMillis, Map<UUID, ScheduledFuture<?>> taskMap) {
        cleanupTask(sessionId, taskMap);
        ScheduledFuture<?> future = executor.schedule(callback, delayMillis, TimeUnit.MILLISECONDS);
        taskMap.put(sessionId, future);
    }

    private void cleanupTask(UUID sessionId, Map<UUID, ScheduledFuture<?>> taskMap) {
        ScheduledFuture<?> future = taskMap.remove(sessionId);
        if (future != null) future.cancel(false);
    }

    @PreDestroy
    public void shutdown() {
        autoStartTasks.values().forEach(future -> future.cancel(false));
        timeoutTasks.values().forEach(future -> future.cancel(false));
        autoStartTasks.clear();
        timeoutTasks.clear();
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
