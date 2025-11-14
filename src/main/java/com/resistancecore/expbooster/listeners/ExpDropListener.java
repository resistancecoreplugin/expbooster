package com.resistancecore.expbooster.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.resistancecore.expbooster.ExpBooster;

public class ExpDropListener implements Listener {
    
    private final ExpBooster plugin;
    
    public ExpDropListener(ExpBooster plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        // Skip if no XP is dropped
        if (event.getDroppedExp() <= 0) {
            return;
        }
        
        // Get original XP
        int originalExp = event.getDroppedExp();
        
        // Check if killer is a player
        LivingEntity entity = event.getEntity(); // EntityDeathEvent always has a LivingEntity
        Player killer = entity.getKiller();
        
        if (killer != null) {
            // Get world multiplier
            String worldName = entity.getWorld().getName();
            double worldMultiplier = plugin.getWorldManager().getEffectiveMultiplier(worldName);
            
            // Get player's active boosters multiplier
            double playerMultiplier = plugin.getBoosterManager().getPlayerTotalMultiplier(killer.getUniqueId());
            
            // Calculate new XP amount (worldMultiplier * playerMultiplier * originalExp)
            double totalMultiplier = worldMultiplier * playerMultiplier;
            int newExp = (int) Math.round(originalExp * totalMultiplier);
            
            // Apply new XP amount
            if (newExp != originalExp) {
                event.setDroppedExp(newExp);
            }
        }
    }
}