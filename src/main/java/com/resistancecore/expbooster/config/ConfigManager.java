package com.resistancecore.expbooster.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.resistancecore.expbooster.ExpBooster;

import java.util.List;

public class ConfigManager {
    
    private final ExpBooster plugin;
    private FileConfiguration config;
    
    public ConfigManager(ExpBooster plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public double getDefaultMultiplier() {
        return config.getDouble("default-multiplier", 1.0);
    }
    
    public ConfigurationSection getWorldSettings() {
        return config.getConfigurationSection("world-settings");
    }
    
    public List<Integer> getBoosterDurationOptions() {
        return config.getIntegerList("boosters.duration-options");
    }
    
    public List<Double> getBoosterMultiplierOptions() {
        return config.getDoubleList("boosters.multiplier-options");
    }
    
    public double getBoosterBasePrice() {
        return config.getDouble("boosters.pricing.base-price", 100.0);
    }
    
    public double getBoosterDurationFactor() {
        return config.getDouble("boosters.pricing.duration-factor", 1.0);
    }
    
    public double getBoosterMultiplierFactor() {
        return config.getDouble("boosters.pricing.multiplier-factor", 1.5);
    }
    
    public String getGuiTitle(String key) {
        return config.getString("gui.title-" + key, "&6&lExp Booster");
    }
    
    public String getMessage(String key) {
        String prefix = config.getString("messages.prefix", "&8[&6ExpBooster&8] ");
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        
        return prefix + message;
    }
    
    public double calculateBoosterPrice(double multiplier, int durationInSeconds) {
        double basePrice = getBoosterBasePrice();
        double durationFactor = getBoosterDurationFactor();
        double multiplierFactor = getBoosterMultiplierFactor();
        
        // Calculate minutes (round up)
        int durationInMinutes = (int) Math.ceil(durationInSeconds / 60.0);
        
        return basePrice * durationInMinutes * durationFactor * multiplier * multiplierFactor;
    }
}