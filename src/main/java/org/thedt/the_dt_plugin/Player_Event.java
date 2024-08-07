package org.thedt.the_dt_plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class Player_Event implements Listener {
    public static String[] key_name =  {"Common Key", "Uncommon Key", "Rare Key", "Epic Key", "Legendary Key", "Event Key"};

    public static The_DT_Plugin root_plugin;

    public Player_Event(The_DT_Plugin main_plugin) {
        root_plugin = main_plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ServerTickManager tickManager = getServer().getServerTickManager();
        if (tickManager.isFrozen()) {
            tickManager.setFrozen(false);
        }
    }


    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Server server = getServer();
        ServerTickManager tickManager = server.getServerTickManager();
        if (server.getOnlinePlayers().size() <= 1) {
            tickManager.setFrozen(true);
            server.savePlayers();
            server.getWorlds().forEach(World::save);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location to = event.getTo();
        Chunk toChunk = to.getChunk();
        to.getWorld().loadChunk(toChunk);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (item.getType() == Material.TRIPWIRE_HOOK && meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore.size() == 1) {
                if (lore.get(0).endsWith(" Key")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public void blockmessage(Player p, String key) {
        p.sendMessage("Hold "+key+" to open crate");
    }

    @EventHandler
    public void onOpenChest(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null) {
            if (block.getType() != Material.CHEST) {
                return;
            }
            Chest chest = (Chest) block.getState();
            String name = chest.getCustomName();
            if (name != null && name.toLowerCase().startsWith("c0fa238ee3|")) {
                ItemStack held = event.getPlayer().getInventory().getItemInMainHand();
                ItemMeta meta = held.getItemMeta();
                if (held.getType() == Material.COMMAND_BLOCK || held.getType() == Material.DEBUG_STICK) {
                    return;
                }
                event.setCancelled(true);
                String[] split = name.split("\\|");
                int needKey = Integer.parseInt(split[1]);
                int keytype = Integer.parseInt(split[2]);
                String keyname = key_name[keytype];
                if (held.getType() == Material.TRIPWIRE_HOOK && meta.hasLore()) {
                    if (!Objects.equals(meta.getLore().get(0), keyname)) {
                        blockmessage(event.getPlayer(), keyname);
                        return;
                    } else if (held.getAmount() < needKey) {
                        event.getPlayer().sendMessage("You don't have enough key");
                        return;
                    } else if (split[2].equals("5") && split[3] != null) {
                        long end_time = Long.parseLong(split[3]);
                        long currentTimeMillis = System.currentTimeMillis();
                        long utcUnixTimeSeconds = currentTimeMillis / 1000;
                        if (utcUnixTimeSeconds > end_time) {
                            event.getPlayer().sendMessage("Event is over");
                            return;
                        }
                    }
                    Random rand = new Random();
                    ArrayList<ItemStack> items = new ArrayList<>();
                    chest.getBlockInventory().forEach(
                            itemStack -> {
                                if (itemStack != null) {
                                    items.add(itemStack);
                                }
                            }
                    );
                    if (items.size() == 0) {
                        event.getPlayer().sendMessage("Chest is empty");
                        return;
                    }
                    ItemStack item = items.get(rand.nextInt(items.size()));
                    held.setAmount(held.getAmount() - needKey);
                    event.getPlayer().sendMessage("Opened "+keyname);
                    event.getPlayer().playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 100, 1);
                    Location loc = block.getLocation();
                    loc.setY(loc.getY() + 1);
                    event.getPlayer().spawnParticle(Particle.EXPLOSION_NORMAL, loc, 50);
                    if (item.getType() == Material.BARRIER) {
                        event.getPlayer().sendMessage("Sory, you have nothing :<");
                    } else {
                        event.getPlayer().playNote(block.getLocation(), Instrument.PIANO, Note.sharp(2, Note.Tone.F));
                        event.getPlayer().getInventory().addItem(item);
                        String iname;
                        if (item.hasItemMeta()) {
                            if (!item.getItemMeta().hasDisplayName()) {
                                iname = item.getType().name().toLowerCase();
                            } else {
                                iname = item.getItemMeta().getDisplayName();
                            }
                        } else {
                            iname = item.getType().name().toLowerCase();
                        }
                        event.getPlayer().sendMessage("You got: x" + item.getAmount() + " " + iname);
                    }
                } else {
                    blockmessage(event.getPlayer(), keyname);
                    return;
                }
            }
        }
    }

}
