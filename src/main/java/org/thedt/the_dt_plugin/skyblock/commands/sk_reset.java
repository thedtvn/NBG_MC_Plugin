package org.thedt.the_dt_plugin.skyblock.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class sk_reset implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (split[0] != "confirm") {
            player.sendMessage("`/sk_reset confirm` to reset your skyblock");
            return true;
        }
        MultiverseCore core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (getServer().getWorld("skyblock-" + player.getName()) == null) {
            player.sendMessage("You don't have skyblock to reset.");
            return true;
        }
        player.sendMessage("Start resetting skyblock.");
        player.teleport(getServer().getWorld("skyblock-" + player.getName()).getSpawnLocation());
        player.getInventory().clear();
        player.teleport(getServer().getWorld("world").getSpawnLocation());
        core.deleteWorld("skyblock-" + player.getName());
        boolean result = core.cloneWorld("skyblock", "skyblock-" + player.getName(), "");
        if (!result) {
            player.sendMessage("Failed to create skyblock");
            return true;
        }
        player.sendMessage("Skyblock has been reset.");
        return true;
    }
}
