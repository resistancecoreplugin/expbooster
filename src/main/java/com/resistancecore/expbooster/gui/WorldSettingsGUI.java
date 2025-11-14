package com.resistancecore.expbooster.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.resistancecore.expbooster.ExpBooster;
import com.resistancecore.expbooster.models.WorldSettings;
import com.resistancecore.expbooster.utils.GuiUtils;
import com.resistancecore.expbooster.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

public class WorldSettingsGUI extends GUI {
    
    private int currentPage = 0;
    private final List<World> worlds;
    private final int worldsPerPage = 21;
    
    public WorldSettingsGUI(ExpBooster plugin, Player player) {
        super(plugin, player, "world-settings", 54);
        worlds = new ArrayList<>(Bukkit.getWorlds());
    }
    
    public WorldSettingsGUI(ExpBooster plugin, Player player, int page) {
        super(plugin, player, "world-settings", 54);
        worlds = new ArrayList<>(Bukkit.getWorlds());
        this.currentPage = page;
    }
    
    @Override
    protected void populate() {
        fillBorder();
        
        // Back button
        setBackButton(49);
        
        // Pagination if needed
        int totalPages = (int) Math.ceil(worlds.size() / (double) worldsPerPage);
        
        if (totalPages > 1) {
            if (currentPage > 0) {
                setItem(45, GuiUtils.createItem("ARROW", "&aPrevious Page"));
            }
            
            if (currentPage < totalPages - 1) {
                setItem(53, GuiUtils.createItem("ARROW", "&aNext Page"));
            }
            
            // Page indicator
            setItem(4, GuiUtils.createItem("PAPER", "&6Page " + (currentPage + 1) + "/" + totalPages));
        }
        
        // World settings
        int startIndex = currentPage * worldsPerPage;
        int endIndex = Math.min(startIndex + worldsPerPage, worlds.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            World world = worlds.get(i);
            int slot = getSlotForIndex(i - startIndex);
            
            WorldSettings settings = plugin.getWorldManager().getWorldSettings(world.getName());
            boolean enabled = settings.isEnabled();
            double multiplier = settings.getMultiplier();
            
            Material icon = Material.GRASS_BLOCK;
            if (world.getEnvironment() == World.Environment.NETHER) {
                icon = Material.NETHERRACK;
            } else if (world.getEnvironment() == World.Environment.THE_END) {
                icon = Material.END_STONE;
            }
            
            List<String> lore = new ArrayList<>();
            lore.add("&7World: &f" + world.getName());
            lore.add("&7Enabled: " + (enabled ? "&aYes" : "&cNo"));
            lore.add("&7Multiplier: &e" + multiplier + "x");
            lore.add("");
            lore.add("&eLeft-click &7to toggle enabled");
            lore.add("&eRight-click &7to change multiplier");
            
            setItem(slot, GuiUtils.createItem(icon, "&6" + world.getName(), lore));
        }
    }
    
    private int getSlotForIndex(int index) {
        int row = index / 7;
        int col = index % 7;
        return 10 + row * 9 + col;
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        // Back button
        if (isBackButton(clickedItem)) {
            new MainGUI(plugin, player).open();
            return;
        }
        
        // Pagination
        if (clickedItem.getItemMeta().getDisplayName().equals(GuiUtils.color("&aPrevious Page"))) {
            new WorldSettingsGUI(plugin, player, currentPage - 1).open();
            return;
        }
        
        if (clickedItem.getItemMeta().getDisplayName().equals(GuiUtils.color("&aNext Page"))) {
            new WorldSettingsGUI(plugin, player, currentPage + 1).open();
            return;
        }
        
        // World setting
        String worldName = GuiUtils.stripColor(clickedItem.getItemMeta().getDisplayName());
        WorldSettings settings = plugin.getWorldManager().getWorldSettings(worldName);
        
        if (event.isLeftClick()) {
            // Toggle enabled
            settings.setEnabled(!settings.isEnabled());
            plugin.getWorldManager().saveWorldSettings();
            new WorldSettingsGUI(plugin, player, currentPage).open();
        } else if (event.isRightClick()) {
            // Change multiplier (use chat input)
            player.closeInventory();
            
            MessageUtils.sendMessage(player, "&aEnter a new multiplier for world &e" + worldName + 
                    " &ain chat (current: &e" + settings.getMultiplier() + "x&a):");
            
            plugin.getServer().getPluginManager().registerEvents(
                new MultiplierInputListener(plugin, player, worldName, currentPage), plugin);
        }
    }
}