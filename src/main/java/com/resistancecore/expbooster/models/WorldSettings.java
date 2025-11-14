package com.resistancecore.expbooster.models;

public class WorldSettings {
    
    private String worldName;
    private boolean enabled;
    private double multiplier;
    
    public WorldSettings(String worldName, boolean enabled, double multiplier) {
        this.worldName = worldName;
        this.enabled = enabled;
        this.multiplier = multiplier;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public double getMultiplier() {
        return multiplier;
    }
    
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}