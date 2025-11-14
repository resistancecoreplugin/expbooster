package com.resistancecore.expbooster.models;

import java.util.UUID;

public class Booster {
    
    private UUID playerUuid;
    private double multiplier;
    private long durationSeconds;
    private long startTime;
    private boolean isGlobal;
    private int taskId = -1;
    
    public Booster(double multiplier, long durationSeconds, boolean isGlobal) {
        this.multiplier = multiplier;
        this.durationSeconds = durationSeconds;
        this.isGlobal = isGlobal;
        this.startTime = System.currentTimeMillis() / 1000; // Current time in seconds
    }
    
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    
    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }
    
    public double getMultiplier() {
        return multiplier;
    }
    
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
    
    public long getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public boolean isGlobal() {
        return isGlobal;
    }
    
    public void setGlobal(boolean global) {
        isGlobal = global;
    }
    
    public int getTaskId() {
        return taskId;
    }
    
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime >= (startTime + durationSeconds);
    }
    
    public long getRemainingTime() {
        long currentTime = System.currentTimeMillis() / 1000;
        long endTime = startTime + durationSeconds;
        
        if (currentTime >= endTime) {
            return 0;
        }
        
        return endTime - currentTime;
    }
}