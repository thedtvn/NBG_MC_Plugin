package org.thedt.the_dt_plugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class H_TP implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (!player.getWorld().getName().startsWith("world")) {
            player.sendMessage("This command only works in the world");
            return true;
        }
        Location bed = player.getRespawnLocation();
        if (bed == null) {
            player.teleport(player.getWorld().getSpawnLocation());
            player.sendMessage("Teleported to spawn");
            return true;
        }
        player.teleport(bed);
        player.sendMessage("Teleported to spawn");
        return true;
    }

}
