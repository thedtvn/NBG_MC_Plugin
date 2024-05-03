package org.thedt.the_dt_plugin;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Entity_Event implements Listener {

    public static The_DT_Plugin root_plugin;

    public static EntityDamageEvent.DamageCause[] instant_dead = {
            EntityDamageEvent.DamageCause.KILL,
            EntityDamageEvent.DamageCause.HOT_FLOOR,
            EntityDamageEvent.DamageCause.FALL,
            EntityDamageEvent.DamageCause.FALLING_BLOCK,
            EntityDamageEvent.DamageCause.LAVA,
            EntityDamageEvent.DamageCause.DROWNING,
            EntityDamageEvent.DamageCause.CONTACT
    };

    public Entity_Event(The_DT_Plugin main_plugin) {
        root_plugin = main_plugin;
    }

    public boolean stackable(Entity entity, Entity other) {
        if (entity.isDead() || other.isDead()) {
            return false;
        } else if (entity.getType() != other.getType()) {
            return false;
        } else if (entity instanceof Ageable ageable && other instanceof Ageable other_ageable) {
            return ageable.isAdult() == other_ageable.isAdult();
        } else if (entity instanceof Slime slime && other instanceof Slime other_slime) {
            return slime.getSize() == other_slime.getSize();
        }
        return true;
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getSpawnCategory() != SpawnCategory.MONSTER && entity.getCategory() != EntityCategory.ILLAGER) {
            return;
        } else if (entity.isDead()) {
            return;
        } else if (entity.isInsideVehicle()) {
            return;
        } else if (entity.getMaxHealth() > 100) {
            return;
        }
        PersistentDataContainer container_entity = entity.getPersistentDataContainer();
        if (!container_entity.getOrDefault(root_plugin.key_drop, PersistentDataType.BOOLEAN, true)) {
            return;
        }
        entity.getNearbyEntities(1, 1, 1).forEach(e -> {
            if (e instanceof LivingEntity le) {
                if (!stackable(entity, le)) {
                    return;
                }
                double le_h = le.getHealth();
                double entity_h = entity.getHealth();
                double health = le_h + entity_h;
                PersistentDataContainer container_le = le.getPersistentDataContainer();
                container_le.set(root_plugin.key_drop, PersistentDataType.BOOLEAN, false);
                int old_size = container_entity.getOrDefault(root_plugin.key_size, PersistentDataType.INTEGER, 1);
                int entity_size = container_le.getOrDefault(root_plugin.key_size, PersistentDataType.INTEGER, 1);
                if (health >= entity.getMaxHealth()) {
                    le.setHealth(health - entity.getMaxHealth());
                } else {
                    entity.setHealth(health);
                    entity_size--;
                }
                container_entity.set(root_plugin.key_size, PersistentDataType.INTEGER, entity_size + old_size);
                entity.setCustomName(entity.getType() + " x" + (entity_size + old_size));
                le.setHealth(0);
            }
        });
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        PersistentDataContainer container_entity = entity.getPersistentDataContainer();
        if (entity.getSpawnCategory() != SpawnCategory.MONSTER && entity.getCategory() != EntityCategory.ILLAGER && entity.isDead()) {
            return;
        }
        boolean drop = container_entity.getOrDefault(root_plugin.key_drop, PersistentDataType.BOOLEAN, true);
        int size = container_entity.getOrDefault(root_plugin.key_size, PersistentDataType.INTEGER, 1);
        if (!drop) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            return;
        }
        if (entity.getLastDamageCause() != null) {
            EntityDamageEvent.DamageCause cause = entity.getLastDamageCause().getCause();
            if (List.of(instant_dead).contains(cause) && size > 1) {
                List<ItemStack> drops = event.getDrops();
                List<ItemStack> multipliedDrops = new ArrayList<>();
                for (ItemStack drop_item : drops) {
                    for (int j = 0; j < size; j++) {
                        multipliedDrops.add(drop_item.clone());
                    }
                }
                event.getDrops().clear();
                event.getDrops().addAll(multipliedDrops);
                double exp_multiplier = ThreadLocalRandom.current().nextDouble(0.5, 0.8);
                double randMultiplier = ThreadLocalRandom.current().nextDouble(0.5, 1);
                int exp = (int) (int) Math.round(exp_multiplier * randMultiplier * (size - 1));
                event.setDroppedExp(event.getDroppedExp() + exp);
                return;
            }
        }
        if ((size - 1) > 0) {
            Entity new_entity = entity.getWorld().spawn(entity.getLocation(), entity.getType().getEntityClass());
            if (new_entity instanceof Ageable ageable) {
                Ageable old_ageable = (Ageable) entity;
                ageable.setAge(old_ageable.getAge());
            } else if (new_entity instanceof Slime slime) {
                Slime old_slime = (Slime) entity;
                slime.setSize(old_slime.getSize());
            }
            if ((size - 1) > 1) {
                new_entity.getPersistentDataContainer().set(root_plugin.key_size, PersistentDataType.INTEGER, size - 1);
                new_entity.setCustomName(entity.getType() + " x" + (size - 1));
            }
        }
        if (container_entity.has(root_plugin.key_size)) {
            container_entity.remove(root_plugin.key_size);
        }
        if (container_entity.has(root_plugin.key_drop)) {
            container_entity.remove(root_plugin.key_drop);
        }
    }

}
