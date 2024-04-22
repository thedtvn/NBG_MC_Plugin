package org.thedt.the_dt_plugin;

import org.bukkit.GameMode;
import org.bukkit.ServerTickManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public final class The_DT_Plugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        getCommand("htp").setExecutor(new H_TP());
        getCommand("spawn").setExecutor(new Spawn());
        pm.registerEvents(new Player_Event(), this);
        ServerTickManager tickManager = getServer().getServerTickManager();
        if (!tickManager.isFrozen() && getServer().getOnlinePlayers().isEmpty()) {
            tickManager.setFrozen(true);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
