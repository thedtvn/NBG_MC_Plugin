package org.thedt.the_dt_plugin.skyblock.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class c_skyblock implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (!player.getWorld().getName().startsWith("world")) {
            player.sendMessage("This command only works in the world");
            return true;
        }
        MultiverseCore core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        assert core != null;
        if (getServer().getWorld("skyblock-"+player.getName()) == null) {
            boolean result = core.cloneWorld("skyblock","skyblock-"+player.getName(), "");
            if (!result) {
                player.sendMessage("Failed to create skyblock");
                return true;
            }
        }
        player.sendMessage("Teleported to skyblock");
        player.teleport(getServer().getWorld("skyblock-"+player.getName()).getSpawnLocation());
        return true;
    }
}
