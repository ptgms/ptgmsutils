package space.ptgms.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnLimit
        implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ptgmsUtils.spawnlimit.set")) {
            sender.sendMessage("You do not have permission to use this command.");
            return false;
        }
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage("Usage: /spawnlimit <get/set> [radius]");
            return false;
        }

        if (Objects.equals(args[0], "set") && args.length != 2) {
            sender.sendMessage("Usage: /spawnlimit set <radius>");
            return false;
        }

        if (Objects.equals(args[0], "get") && args.length != 1) {
            sender.sendMessage("Usage: /spawnlimit get");
            return false;
        }

        if (Objects.equals(args[0], "get")) {
            sender.sendMessage("The spawn limit is " + ChatColor.DARK_PURPLE + ChatColor.BOLD + Main.getPlugin(Main.class).getConfig().getInt("spawnLimit") + ChatColor.RESET + ".");
            return true;
        }

        int radius = Integer.parseInt(args[1]);

        if (radius < 0) {
            sender.sendMessage("Radius must be positive.");
            return false;
        }

        if (radius > 100) {
            sender.sendMessage("Radius must be less than 100.");
            return false;
        }

        FileConfiguration config = Main.getPlugin(Main.class).getConfig();

        config.set("spawnLimit", radius);

        Main.getPlugin(Main.class).saveConfig();

        sender.getServer().broadcastMessage(
                ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + sender.getName() + ChatColor.RESET +
                        " has set the spawn limit to " + ChatColor.DARK_PURPLE + ChatColor.BOLD + radius +
                        ChatColor.RESET + ".");


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("ptgmsUtils.spawnlimit.set")) {
                return List.of("get", "set");
            }
        } else if (args.length == 1) {
            if (sender.hasPermission("ptgmsUtils.spawnlimit.set")) {
                if (args[0] == null || args[0].isEmpty()) {
                    return List.of("get", "set");
                }
                if ("set".startsWith(args[0].toLowerCase())) {
                    return List.of("set");
                } else if ("get".startsWith(args[0])) {
                    return List.of("get");
                }
            }
        } else if (args.length == 2 && Objects.equals(args[0], "set")) {
            if (sender.hasPermission("ptgmsUtils.spawnlimit.set")) {
                return List.of("<radius>");
            }
        }
        return new LinkedList<>();
    }
}
