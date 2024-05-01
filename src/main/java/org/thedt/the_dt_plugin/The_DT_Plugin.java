package org.thedt.the_dt_plugin;

import org.bukkit.NamespacedKey;
import org.bukkit.ServerTickManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class The_DT_Plugin extends JavaPlugin {

    public NamespacedKey key_size = new NamespacedKey(this, "e_size");
    public NamespacedKey key_drop = new NamespacedKey(this, "e_drop");

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        getCommand("htp").setExecutor(new H_TP());
        getCommand("spawn").setExecutor(new Spawn());
        pm.registerEvents(new Player_Event(this), this);
        pm.registerEvents(new Entity_Event(this), this);
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
