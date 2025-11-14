package com.resistancecore.expbooster;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.resistancecore.expbooster.commands.CommandHandler;
import com.resistancecore.expbooster.config.ConfigManager;
import com.resistancecore.expbooster.gui.GUIHandler;
import com.resistancecore.expbooster.listeners.ExpDropListener;
import com.resistancecore.expbooster.managers.BoosterManager;
import com.resistancecore.expbooster.managers.WorldManager;

public class ExpBooster extends JavaPlugin {
    
    private static ExpBooster instance;
    private ConfigManager configManager;
    private WorldManager worldManager;
    private BoosterManager boosterManager;
    private GUIHandler guiHandler;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize config
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        
        // Setup economy
        if (!setupEconomy()) {
            getLogger().severe("Vault dependency not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize managers
        worldManager = new WorldManager(this);
        boosterManager = new BoosterManager(this);
        guiHandler = new GUIHandler(this);
        
        // Register events
        getServer().getPluginManager().registerEvents(new ExpDropListener(this), this);
        
        // Register commands
        getCommand("expbooster").setExecutor(new CommandHandler(this));
        
        // Load data
        worldManager.loadWorldSettings();
        
        getLogger().info("ExpBooster has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save data
        worldManager.saveWorldSettings();
        boosterManager.cancelAllBoosters();
        
        getLogger().info("ExpBooster has been disabled!");
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        
        economy = rsp.getProvider();
        return economy != null;
    }
    
    public static ExpBooster getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public WorldManager getWorldManager() {
        return worldManager;
    }
    
    public BoosterManager getBoosterManager() {
        return boosterManager;
    }
    
    public GUIHandler getGuiHandler() {
        return guiHandler;
    }
    
    public Economy getEconomy() {
        return economy;
    }
}