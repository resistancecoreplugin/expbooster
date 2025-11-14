package com.resistancecore.expbooster.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.resistancecore.expbooster.ExpBooster;
import com.resistancecore.expbooster.models.Booster;
import com.resistancecore.expbooster.utils.MessageUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BoosterManager {
    
    private final ExpBooster plugin;
    private final Map<UUID, List<Booster>> playerBoosters = new ConcurrentHashMap<>();
    private Booster globalBooster = null;
    
    public BoosterManager(ExpBooster plugin) {
        this.plugin = plugin;
    }
    
    public void activatePlayerBooster(UUID playerUuid, Booster booster) {
        // Cancel any task that may be associated with this booster
        if (booster.getTaskId() != -1) {
            Bukkit.getScheduler().cancelTask(booster.getTaskId());
        }
        
        // Set player UUID
        booster.setPlayerUuid(playerUuid);
        
        // Add booster to player's list
        playerBoosters.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(booster);
        
        // Schedule task to remove booster when it expires
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (booster.isExpired()) {
                    removePlayerBooster(playerUuid, booster);
                    
                    // Send expiration message if player is online
                    Player player = Bukkit.getPlayer(playerUuid);
                    if (player != null) {
                        String message = plugin.getConfigManager().getMessage("booster-expired")
                                .replace("%multiplier%", String.valueOf(booster.getMultiplier()));
                        MessageUtils.sendMessage(player, message);
                    }
                    
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Check every second
        
        booster.setTaskId(task.getTaskId());
    }
    
    public void removePlayerBooster(UUID playerUuid, Booster booster) {
        // Cancel task
        if (booster.getTaskId() != -1) {
            Bukkit.getScheduler().cancelTask(booster.getTaskId());
        }
        
        // Remove booster from player's list
        if (playerBoosters.containsKey(playerUuid)) {
            playerBoosters.get(playerUuid).remove(booster);
            
            // Remove player entry if no boosters left
            if (playerBoosters.get(playerUuid).isEmpty()) {
                playerBoosters.remove(playerUuid);
            }
        }
    }
    
    public void activateGlobalBooster(Booster booster) {
        // Cancel any existing global booster
        cancelGlobalBooster();
        
        // Set the new global booster
        globalBooster = booster;
        
        // Schedule task to remove booster when it expires
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (globalBooster != null && globalBooster.isExpired()) {
                    String message = plugin.getConfigManager().getMessage("global-booster-expired")
                            .replace("%multiplier%", String.valueOf(globalBooster.getMultiplier()));
                    MessageUtils.broadcastMessage(message);
                    
                    globalBooster = null;
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Check every second
        
        booster.setTaskId(task.getTaskId());
    }
    
    public void cancelGlobalBooster() {
        if (globalBooster != null && globalBooster.getTaskId() != -1) {
            Bukkit.getScheduler().cancelTask(globalBooster.getTaskId());
            globalBooster = null;
        }
    }
    
    public void cancelAllBoosters() {
        // Cancel all player boosters
        for (List<Booster> boosters : playerBoosters.values()) {
            for (Booster booster : boosters) {
                if (booster.getTaskId() != -1) {
                    Bukkit.getScheduler().cancelTask(booster.getTaskId());
                }
            }
        }
        
        // Clear player boosters
        playerBoosters.clear();
        
        // Cancel global booster
        cancelGlobalBooster();
    }
    
    public List<Booster> getPlayerBoosters(UUID playerUuid) {
        return playerBoosters.getOrDefault(playerUuid, Collections.emptyList());
    }
    
    public Booster getGlobalBooster() {
        if (globalBooster != null && globalBooster.isExpired()) {
            cancelGlobalBooster();
            return null;
        }
        return globalBooster;
    }
    
    public double getPlayerTotalMultiplier(UUID playerUuid) {
        double multiplier = 1.0;
        
        // Apply global booster if exists
        Booster global = getGlobalBooster();
        if (global != null && !global.isExpired()) {
            multiplier *= global.getMultiplier();
        }
        
        // Apply player boosters
        List<Booster> boosters = new ArrayList<>(getPlayerBoosters(playerUuid));
        for (Booster booster : boosters) {
            if (booster.isExpired()) {
                removePlayerBooster(playerUuid, booster);
            } else {
                multiplier *= booster.getMultiplier();
            }
        }
        
        return multiplier;
    }
}