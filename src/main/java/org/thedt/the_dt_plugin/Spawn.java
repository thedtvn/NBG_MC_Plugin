package org.thedt.the_dt_plugin;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class Spawn implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player player)) {
            return false;
        };
        World world = getServer().getWorld("world");
        assert world != null;
        player.teleport(world.getSpawnLocation());
        player.sendMessage("Teleported to spawn");
        return true;
    }
}