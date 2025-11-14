package com.resistancecore.expbooster.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
    
    /**
     * Sends a colored message to a command sender.
     * 
     * @param sender The recipient of the message
     * @param message The message to send
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }
    
    /**
     * Broadcasts a colored message to all online players.
     * 
     * @param message The message to broadcast
     */
    public static void broadcastMessage(String message) {
        Bukkit.broadcastMessage(color(message));
    }
    
    /**
     * Sends a colored message to all players with a specific permission.
     * 
     * @param message The message to send
     * @param permission The required permission
     */
    public static void broadcastMessage(String message, String permission) {
        String coloredMessage = color(message);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(coloredMessage);
            }
        }
        
        // Also send to console
        Bukkit.getConsoleSender().sendMessage(coloredMessage);
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
}