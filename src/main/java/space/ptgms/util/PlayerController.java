package space.ptgms.util;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class PlayerController implements Listener {

    Plugin plugin = Main.getPlugin(Main.class);

    public PlayerController(Main plugin) {
        getServer().getPluginManager().registerEvents(this, plugin);
        createTask();
    }

    public void createTask() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (!player.hasPermission("ptgmsUtils.spawnlimit")) {
                        // check if player is outside of spawn in x or z
                        Location playerLocation = player.getLocation();
                        Location spawnLocation = player.getWorld().getSpawnLocation();
                        if (playerLocation.getX() > spawnLocation.getX() + plugin.getConfig().getInt("spawnLimit")
                                || playerLocation.getX() < spawnLocation.getX() - plugin.getConfig().getInt("spawnLimit")
                                || playerLocation.getZ() > spawnLocation.getZ() + plugin.getConfig().getInt("spawnLimit")
                                || playerLocation.getZ() < spawnLocation.getZ() - plugin.getConfig().getInt("spawnLimit")) {
                            player.playSound(player.getLocation(), "minecraft:entity.enderman.teleport", 1, 1);
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "You aren't allowed to go that far from spawn!");
                            player.teleport(player.getWorld().getSpawnLocation());
                        }
                    }
                });
            }
        }, 20, 20);
    }

    @EventHandler
    public void onBlockDamageEvent(BlockDamageEvent event) {
        if (!event.getPlayer().hasPermission("ptgmsUtils.spawnlimit")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "You aren't allowed to break any blocks!");
        }
    }

    // check if mining spawner and give player spawner if they have permission
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (event.getBlock().getType().toString().contains("SPAWNER")) {
            if (event.getPlayer().hasPermission("ptgmsUtils.spawner")) {
                // check if mined with silk touch pickaxe
                if (!event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "You need a silk touch pickaxe to mine spawners!");
                    return;
                }
                BlockState state = event.getBlock().getState();

                if (!(state instanceof CreatureSpawner)) {
                    return;
                }

                if (((CreatureSpawner) state).getSpawnedType() == null) {
                    return;
                }

                var item = new ItemStack(Material.SPAWNER, 1);
                var blockMeta = item.getItemMeta();
                assert blockMeta != null;
                String mobName = mobIDToName(Objects.requireNonNull((((CreatureSpawner) state).getSpawnedType()).getName()));
                NamespacedKey key = new NamespacedKey(plugin, "spawner-type");
                blockMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + String.format("%s Spawner", mobName));
                blockMeta.setLore(List.of(" ", String.format("Place this to spawn %ss", mobName)));
                blockMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, Objects.requireNonNull((((CreatureSpawner) state).getSpawnedType()).getName()));
                blockMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                item.setItemMeta(blockMeta);

                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);

            }
        }
    }

    private String mobIDToName(String mobId) {
        String[] step1 = mobId.toLowerCase().split("_");
        for (int i = 0; i < step1.length; i++) {
            step1[i] = step1[i].substring(0, 1).toUpperCase() + step1[i].substring(1);
        }
        return String.join(" ", step1);
    }

    // when spawner is placed, check if player has permission to place it
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.SPAWNER) {
            if (!event.getPlayer().hasPermission("ptgmsUtils.spawner")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "You aren't allowed to place spawners!");
            }
            event.getBlock().setType(Material.SPAWNER);
            BlockState state = event.getBlock().getState();
            if (!(state instanceof CreatureSpawner)) {
                return;
            }
            var blockMeta = event.getItemInHand().getItemMeta();
            assert blockMeta != null;
            // get the mob type from the item using the key
            NamespacedKey key = new NamespacedKey(plugin, "spawner-type");
            String mobType = blockMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (mobType == null) {
                return;
            }
            ((CreatureSpawner) state).setSpawnedType(EntityType.valueOf(mobType.toUpperCase()));
            state.update();
        }
    }

}
