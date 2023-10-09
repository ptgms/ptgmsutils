package space.ptgms.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

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

}
