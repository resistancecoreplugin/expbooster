package com.resistancecore.expbooster.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiUtils {
    
    /**
     * Creates an item stack with the specified material, name, and lore.
     * 
     * @param material Material name (UPPERCASE)
     * @param name Item name (supports color codes)
     * @param lore Item lore (supports color codes, can be null)
     * @return Created ItemStack
     */
    public static ItemStack createItem(String material, String name) {
        return createItem(material, name, (List<String>) null);
    }
    
    /**
     * Creates an item stack with the specified material, name, and lore.
     * 
     * @param material Material name (UPPERCASE)
     * @param name Item name (supports color codes)
     * @param lore Item lore (supports color codes, can be null)
     * @return Created ItemStack
     */
    public static ItemStack createItem(String material, String name, String... lore) {
        List<String> loreList = null;
        if (lore != null && lore.length > 0) {
            loreList = new ArrayList<>();
            for (String line : lore) {
                loreList.add(color(line));
            }
        }
        
        return createItem(material, name, loreList);
    }
    
    /**
     * Creates an item stack with the specified material, name, and lore.
     * 
     * @param material Material name (UPPERCASE)
     * @param name Item name (supports color codes)
     * @param lore Item lore (supports color codes, can be null)
     * @return Created ItemStack
     */
    public static ItemStack createItem(String material, String name, List<String> lore) {
        Material mat = Material.valueOf(material);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(color(name));
            
            if (lore != null && !lore.isEmpty()) {
                List<String> coloredLore = lore.stream()
                        .map(GuiUtils::color)
                        .collect(Collectors.toList());
                meta.setLore(coloredLore);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Creates an item stack with the specified material, name, and lore.
     * 
     * @param material Material object
     * @param name Item name (supports color codes)
     * @param lore Item lore (supports color codes, can be null)
     * @return Created ItemStack
     */
    public static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(color(name));
            
            if (lore != null && !lore.isEmpty()) {
                List<String> coloredLore = lore.stream()
                        .map(GuiUtils::color)
                        .collect(Collectors.toList());
                meta.setLore(coloredLore);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Translates color codes in the given string.
     * 
     * @param text Text with color codes
     * @return Colored text
     */
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    /**
     * Removes color codes from the given string.
     * 
     * @param text Colored text
     * @return Plain text
     */
    public static String stripColor(String text) {
        return ChatColor.stripColor(color(text));
    }
}