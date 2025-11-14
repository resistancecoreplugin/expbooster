package com.resistancecore.expbooster.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.resistancecore.expbooster.ExpBooster;
import com.resistancecore.expbooster.models.WorldSettings;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {
    
    private final ExpBooster plugin;
    private final Map<String, WorldSettings> worldSettingsMap = new HashMap<>();
    
    public WorldManager(ExpBooster plugin) {
        this.plugin = plugin;
    }
    
    public void loadWorldSettings() {
        worldSettingsMap.clear();
        
        // Load settings from config
        ConfigurationSection section = plugin.getConfigManager().getWorldSettings();
        if (section != null) {
            for (String worldName : section.getKeys(false)) {
                boolean enabled = section.getBoolean(worldName + ".enabled", true);
                double multiplier = section.getDouble(worldName + ".multiplier", 1.0);
                
                worldSettingsMap.put(worldName, new WorldSettings(worldName, enabled, multiplier));
            }
        }
        
        // Add missing worlds with default settings
        double defaultMultiplier = plugin.getConfigManager().getDefaultMultiplier();
        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            if (!worldSettingsMap.containsKey(worldName)) {
                worldSettingsMap.put(worldName, new WorldSettings(worldName, true, defaultMultiplier));
            }
        }
    }
    
    public void saveWorldSettings() {
        // Get the config section
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("world-settings");
        if (section == null) {
            section = plugin.getConfig().createSection("world-settings");
        }
        
        // Save settings to config
        for (WorldSettings settings : worldSettingsMap.values()) {
            String worldName = settings.getWorldName();
            section.set(worldName + ".enabled", settings.isEnabled());
            section.set(worldName + ".multiplier", settings.getMultiplier());
        }
        
        // Save config
        plugin.saveConfig();
    }
    
    public WorldSettings getWorldSettings(String worldName) {
        // Return settings if they exist
        if (worldSettingsMap.containsKey(worldName)) {
            return worldSettingsMap.get(worldName);
        }
        
        // Create default settings if they don't exist
        double defaultMultiplier = plugin.getConfigManager().getDefaultMultiplier();
        WorldSettings settings = new WorldSettings(worldName, true, defaultMultiplier);
        worldSettingsMap.put(worldName, settings);
        
        return settings;
    }
    
    public double getEffectiveMultiplier(String worldName) {
        WorldSettings settings = getWorldSettings(worldName);
        
        // If disabled, return 1.0 (no multiplier)
        if (!settings.isEnabled()) {
            return 1.0;
        }
        
        return settings.getMultiplier();
    }
    
    public void setWorldMultiplier(String worldName, double multiplier) {
        WorldSettings settings = getWorldSettings(worldName);
        settings.setMultiplier(multiplier);
        saveWorldSettings();
    }
    
    public void setWorldEnabled(String worldName, boolean enabled) {
        WorldSettings settings = getWorldSettings(worldName);
        settings.setEnabled(enabled);
        saveWorldSettings();
    }
}