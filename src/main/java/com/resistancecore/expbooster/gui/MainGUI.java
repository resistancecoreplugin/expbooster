package com.resistancecore.expbooster.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import com.resistancecore.expbooster.ExpBooster;
import com.resistancecore.expbooster.models.Booster;
import com.resistancecore.expbooster.utils.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class MainGUI extends GUI {
    
    public MainGUI(ExpBooster plugin, Player player) {
        super(plugin, player, "main", 27);
    }
    
    @Override
    protected void populate() {
        fillBorder();
        
        // World settings button
        if (player.hasPermission("expbooster.admin")) {
            ItemStack worldSettingsItem = GuiUtils.createItem(
                "GRASS_BLOCK", 
                "&6&lWorld Settings", 
                "&7Click to configure XP multipliers per world"
            );
            setItem(11, worldSettingsItem);
        }
        
        // Booster shop button
        ItemStack shopItem = GuiUtils.createItem(
            "EXPERIENCE_BOTTLE", 
            "&6&lBooster Shop", 
            "&7Click to purchase XP boosters"
        );
        setItem(13, shopItem);
        
        // Active boosters button
        ItemStack activeBoostersItem = createActiveBoostersItem();
        setItem(15, activeBoostersItem);
    }
    
    private ItemStack createActiveBoostersItem() {
        List<Booster> playerBoosters = plugin.getBoosterManager().getPlayerBoosters(player.getUniqueId());
        Booster globalBooster = plugin.getBoosterManager().getGlobalBooster();
        
        List<String> lore = new ArrayList<>();
        lore.add("&7Active boosters:");
        
        if (playerBoosters.isEmpty() && globalBooster == null) {
            lore.add("&cNo active boosters");
        } else {
            if (globalBooster != null) {
                long remainingMinutes = globalBooster.getRemainingTime() / 60;
                lore.add(String.format("&a- Global: &ex%s &7(&e%d &7minutes remaining)", 
                    globalBooster.getMultiplier(), remainingMinutes));
            }
            
            for (Booster booster : playerBoosters) {
                long remainingMinutes = booster.getRemainingTime() / 60;
                lore.add(String.format("&a- Personal: &ex%s &7(&e%d &7minutes remaining)", 
                    booster.getMultiplier(), remainingMinutes));
            }
        }
        
        lore.add("");
        lore.add("&7Click to refresh");
        
        return GuiUtils.createItem("CLOCK", "&6&lActive Boosters", lore);
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        String itemName = clickedItem.getItemMeta().getDisplayName();
        
        if (itemName.equals(GuiUtils.color("&6&lWorld Settings")) && player.hasPermission("expbooster.admin")) {
            new WorldSettingsGUI(plugin, player).open();
        } else if (itemName.equals(GuiUtils.color("&6&lBooster Shop"))) {
            new BoosterShopGUI(plugin, player).open();
        } else if (itemName.equals(GuiUtils.color("&6&lActive Boosters"))) {
            // Refresh the menu
            new MainGUI(plugin, player).open();
        }
    }
}