package com.resistancecore.expbooster.gui;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.resistancecore.expbooster.ExpBooster;
import com.resistancecore.expbooster.models.Booster;
import com.resistancecore.expbooster.utils.GuiUtils;
import com.resistancecore.expbooster.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

public class BoosterShopGUI extends GUI {
    
    private boolean showingGlobalBoosters = false;
    
    public BoosterShopGUI(ExpBooster plugin, Player player) {
        super(plugin, player, "shop", 54);
    }
    
    public BoosterShopGUI(ExpBooster plugin, Player player, boolean showingGlobalBoosters) {
        super(plugin, player, "shop", 54);
        this.showingGlobalBoosters = showingGlobalBoosters;
    }
    
    @Override
    protected void populate() {
        fillBorder();
        
        // Back button
        setBackButton(49);
        
        // Toggle between personal and global boosters
        ItemStack toggleItem = GuiUtils.createItem(
            showingGlobalBoosters ? "BEACON" : "EXPERIENCE_BOTTLE",
            showingGlobalBoosters ? "&6&lGlobal Boosters" : "&6&lPersonal Boosters", 
            "&7Click to switch to " + (showingGlobalBoosters ? "personal" : "global") + " boosters"
        );
        setItem(4, toggleItem);
        
        // Booster options
        populateBoosterOptions();
    }
    
    private void populateBoosterOptions() {
        List<Double> multipliers = plugin.getConfigManager().getBoosterMultiplierOptions();
        List<Integer> durations = plugin.getConfigManager().getBoosterDurationOptions();
        
        Economy economy = plugin.getEconomy();
        
        int slot = 10;
        for (Double multiplier : multipliers) {
            for (Integer duration : durations) {
                // Skip if we've run out of slots
                if (slot >= 44) continue;
                
                // Skip slots at the border
                if (slot % 9 == 0 || slot % 9 == 8) {
                    slot++;
                    continue;
                }
                
                int durationMinutes = duration / 60;
                double price = plugin.getConfigManager().calculateBoosterPrice(multiplier, duration);
                
                boolean canAfford = economy.has(player, price);
                
                List<String> lore = new ArrayList<>();
                lore.add("&7Multiplier: &e" + multiplier + "x");
                lore.add("&7Duration: &e" + durationMinutes + " minutes");
                lore.add("&7Price: &e" + String.format("%.2f", price));
                lore.add("");
                
                if (canAfford) {
                    lore.add("&aClick to purchase");
                } else {
                    lore.add("&cYou cannot afford this booster");
                }
                
                Material material = canAfford ? Material.EXPERIENCE_BOTTLE : Material.BARRIER;
                if (showingGlobalBoosters) {
                    material = canAfford ? Material.BEACON : Material.BARRIER;
                    
                    // Admin permission check for global boosters
                    if (!player.hasPermission("expbooster.admin")) {
                        material = Material.BARRIER;
                        lore.clear();
                        lore.add("&cYou don't have permission to purchase global boosters");
                    }
                }
                
                setItem(slot, GuiUtils.createItem(
                    material,
                    "&6" + multiplier + "x for " + durationMinutes + " minutes",
                    lore
                ));
                
                slot++;
            }
        }
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
        
        // Toggle button
        if (clickedItem.getItemMeta().getDisplayName().equals(GuiUtils.color("&6&lGlobal Boosters")) ||
            clickedItem.getItemMeta().getDisplayName().equals(GuiUtils.color("&6&lPersonal Boosters"))) {
            new BoosterShopGUI(plugin, player, !showingGlobalBoosters).open();
            return;
        }
        
        // Don't process clicks on barriers
        if (clickedItem.getType() == Material.BARRIER) {
            return;
        }
        
        // Process booster purchase
        String displayName = GuiUtils.stripColor(clickedItem.getItemMeta().getDisplayName());
        String[] parts = displayName.split(" ");
        
        try {
            // Parse multiplier and duration
            double multiplier = Double.parseDouble(parts[0].replace("x", ""));
            int durationMinutes = Integer.parseInt(parts[2]);
            int durationSeconds = durationMinutes * 60;
            
            // Calculate price
            double price = plugin.getConfigManager().calculateBoosterPrice(multiplier, durationSeconds);
            
            // Check if player has enough money
            Economy economy = plugin.getEconomy();
            if (!economy.has(player, price)) {
                MessageUtils.sendMessage(player, plugin.getConfigManager().getMessage("insufficient-funds"));
                return;
            }
            
            // Withdraw money
            economy.withdrawPlayer(player, price);
            
            // Create and activate booster
            if (showingGlobalBoosters) {
                if (!player.hasPermission("expbooster.admin")) {
                    return;
                }
                
                Booster booster = new Booster(multiplier, durationSeconds, true);
                plugin.getBoosterManager().activateGlobalBooster(booster);
                
                // Send global message
                String message = plugin.getConfigManager().getMessage("global-booster-activated")
                        .replace("%multiplier%", String.valueOf(multiplier))
                        .replace("%duration%", String.valueOf(durationMinutes));
                MessageUtils.broadcastMessage(message);
            } else {
                Booster booster = new Booster(multiplier, durationSeconds, false);
                booster.setPlayerUuid(player.getUniqueId());
                plugin.getBoosterManager().activatePlayerBooster(player.getUniqueId(), booster);
                
                // Send personal message
                String message = plugin.getConfigManager().getMessage("booster-activated")
                        .replace("%multiplier%", String.valueOf(multiplier))
                        .replace("%duration%", String.valueOf(durationMinutes));
                MessageUtils.sendMessage(player, message);
            }
            
            // Close GUI and go back to main menu
            new MainGUI(plugin, player).open();
            
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(player, "&cAn error occurred while processing your purchase.");
            e.printStackTrace();
        }
    }
}