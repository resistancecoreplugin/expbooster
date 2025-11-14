package com.resistancecore.expbooster.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import com.resistancecore.expbooster.ExpBooster;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIHandler implements Listener {
    
    private final ExpBooster plugin;
    private final Map<UUID, GUI> openGuis = new HashMap<>();
    
    public GUIHandler(ExpBooster plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void registerGUI(Player player, GUI gui) {
        openGuis.put(player.getUniqueId(), gui);
    }
    
    public void unregisterGUI(Player player) {
        openGuis.remove(player.getUniqueId());
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        GUI gui = openGuis.get(player.getUniqueId());
        if (gui != null && event.getView().getTopInventory().equals(event.getClickedInventory())) {
            gui.handleClick(event);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        
        GUI gui = openGuis.get(player.getUniqueId());
        if (gui != null && event.getInventory().equals(gui.inventory)) {
            unregisterGUI(player);
        }
    }
}