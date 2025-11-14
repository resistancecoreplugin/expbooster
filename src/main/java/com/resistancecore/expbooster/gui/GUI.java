package com.resistancecore.expbooster.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.resistancecore.expbooster.ExpBooster;
import com.resistancecore.expbooster.utils.GuiUtils;

public abstract class GUI {
    
    protected final ExpBooster plugin;
    protected final Player player;
    protected Inventory inventory;
    protected String title;
    protected int size;
    
    public GUI(ExpBooster plugin, Player player, String titleKey, int size) {
        this.plugin = plugin;
        this.player = player;
        this.title = GuiUtils.color(plugin.getConfigManager().getGuiTitle(titleKey));
        this.size = size;
    }
    
    public void open() {
        inventory = Bukkit.createInventory(null, size, title);
        populate();
        player.openInventory(inventory);
        
        // Register GUI in handler
        plugin.getGuiHandler().registerGUI(player, this);
    }
    
    protected abstract void populate();
    
    public abstract void handleClick(InventoryClickEvent event);
    
    protected void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }
    
    protected void fillBorder() {
        ItemStack borderItem = GuiUtils.createItem("BLACK_STAINED_GLASS_PANE", " ");
        
        // Top and bottom rows
        for (int i = 0; i < 9; i++) {
            setItem(i, borderItem);
            setItem(size - 9 + i, borderItem);
        }
        
        // Left and right columns
        for (int i = 9; i < size - 9; i += 9) {
            setItem(i, borderItem);
            setItem(i + 8, borderItem);
        }
    }
    
    protected void setBackButton(int slot) {
        ItemStack backButton = GuiUtils.createItem("ARROW", "&cBack");
        setItem(slot, backButton);
    }
    
    protected boolean isBackButton(ItemStack item) {
        return item != null && item.getType().name().equals("ARROW") && 
               item.getItemMeta() != null && 
               item.getItemMeta().getDisplayName().equals(GuiUtils.color("&cBack"));
    }
}