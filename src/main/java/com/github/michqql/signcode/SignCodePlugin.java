package com.github.michqql.signcode;

import com.github.michqql.signcode.listeners.BlockInteractListener;
import com.github.michqql.signcode.listeners.SignListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignCodePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        VaultManager manager = new VaultManager();

        Bukkit.getPluginManager().registerEvents(new BlockInteractListener(this, manager), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(manager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
