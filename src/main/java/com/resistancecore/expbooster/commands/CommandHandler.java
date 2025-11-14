package com.resistancecore.expbooster.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.resistancecore.expbooster.ExpBooster;
import com.resistancecore.expbooster.gui.MainGUI;
import com.resistancecore.expbooster.utils.MessageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {
    
    private final ExpBooster plugin;
    
    public CommandHandler(ExpBooster plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.sendMessage(sender, "&cThis command can only be used by players.");
            return true;
        }
        
        if (args.length == 0) {
            // Open main GUI
            if (!player.hasPermission("expbooster.use")) {
                MessageUtils.sendMessage(player, plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }
            
            new MainGUI(plugin, player).open();
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!player.hasPermission("expbooster.admin")) {
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                
                plugin.reloadConfig();
                plugin.getConfigManager().reloadConfig();
                plugin.getWorldManager().loadWorldSettings();
                MessageUtils.sendMessage(player, "&aConfiguration reloaded!");
                return true;
            }
            case "help" -> {
                MessageUtils.sendMessage(player, "&6=== ExpBooster Help ===");
                MessageUtils.sendMessage(player, "&e/expbooster &7- Open the main GUI");
                if (player.hasPermission("expbooster.admin")) {
                    MessageUtils.sendMessage(player, "&e/expbooster reload &7- Reload the configuration");
                }
                return true;
            }
            default -> {
                MessageUtils.sendMessage(player, "&cUnknown command. Type &e/expbooster help &cfor help.");
                return true;
            }
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("help");
            
            if (sender.hasPermission("expbooster.admin")) {
                completions.add("reload");
            }
            
            return completions.stream()
                    .filter(c -> c.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
}