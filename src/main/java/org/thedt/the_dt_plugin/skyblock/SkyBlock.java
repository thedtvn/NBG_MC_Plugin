package org.thedt.the_dt_plugin.skyblock;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.thedt.the_dt_plugin.The_DT_Plugin;
import org.thedt.the_dt_plugin.helper.WeightedRandomBag;

public class SkyBlock implements Listener {
    public static WeightedRandomBag<Material> random_block = new WeightedRandomBag<>();
    public static The_DT_Plugin root_plugin;

    public SkyBlock(The_DT_Plugin main_plugin) {
        root_plugin = main_plugin;
        random_block.addEntry(Material.COBBLESTONE, 50);
        random_block.addEntry(Material.COAL_ORE, 25);
        random_block.addEntry(Material.IRON_ORE, 12);
        random_block.addEntry(Material.GOLD_ORE, 3);
        random_block.addEntry(Material.EMERALD_ORE, 2);
        random_block.addEntry(Material.REDSTONE_ORE, 2);
        random_block.addEntry(Material.DIAMOND_ORE, 1);
    }

    public boolean checkIsSkyBlock(World world) {
        return world.getName().startsWith("skyblock");
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        if (!checkIsSkyBlock(event.getBlock().getWorld())) {
            return;
        }
        BlockState blockState = event.getNewState();
        if (blockState.getType() != Material.COBBLESTONE) {
            return;
        }
        blockState.setType(random_block.getRandom());
    }

    @EventHandler
    public void portal_block(PlayerPortalEvent event){
        if (!checkIsSkyBlock(event.getPlayer().getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

}
