package com.resistancecore.expbooster.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import com.resistancecore.expbooster.ExpBooster;
import com.resistancecore.expbooster.utils.MessageUtils;

public class MultiplierInputListener implements Listener {
    
    private final ExpBooster plugin;
    private final Player player;
    private final String worldName;
    private final int currentPage;
    
    public MultiplierInputListener(ExpBooster plugin, Player player, String worldName, int currentPage) {
        this.plugin = plugin;
        this.player = player;
        this.worldName = worldName;
        this.currentPage = currentPage;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer() != player) {
            return;
        }
        
        event.setCancelled(true);
        String input = event.getMessage();
        
        // Cancel the listener
        HandlerList.unregisterAll(this);
        
        // Process input on the main thread
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    double multiplier = Double.parseDouble(input);
                    
                    if (multiplier <= 0) {
                        MessageUtils.sendMessage(player, "&cMultiplier must be greater than 0.");
                        new WorldSettingsGUI(plugin, player, currentPage).open();
                        return;
                    }
                    
                    // Update the multiplier
                    plugin.getWorldManager().setWorldMultiplier(worldName, multiplier);
                    
                    // Send confirmation message
                    String message = plugin.getConfigManager().getMessage("world-setting-updated")
                            .replace("%world%", worldName)
                            .replace("%multiplier%", String.valueOf(multiplier));
                    MessageUtils.sendMessage(player, message);
                    
                    // Reopen the GUI
                    new WorldSettingsGUI(plugin, player, currentPage).open();
                    
                } catch (NumberFormatException e) {
                    MessageUtils.sendMessage(player, "&cInvalid number format. Please enter a valid number.");
                    new WorldSettingsGUI(plugin, player, currentPage).open();
                }
            }
        }.runTask(plugin);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer() == player) {
            HandlerList.unregisterAll(this);
        }
    }
}