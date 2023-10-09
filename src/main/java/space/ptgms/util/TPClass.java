package space.ptgms.util;

import java.util.LinkedList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TPClass implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command!");
            return false;
        }
        if (sender.hasPermission("tpwild.wild")) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Teleporting...");
            int x = (int) (Math.random() * sender.getServer().getMaxWorldSize() * 2) - sender.getServer().getMaxWorldSize();
            int z = (int) (Math.random() * sender.getServer().getMaxWorldSize() * 2) - sender.getServer().getMaxWorldSize();
            Location pos = sender.getServer().getWorlds().get(0).getHighestBlockAt(x, z).getLocation();
            pos.add(0, 1, 0);
//            check if its in water
            int failsafe = 0;
            if (args.length == 1 && args[0].equalsIgnoreCase("unsafe")) {
                // user specified they are fine with water
                failsafe = 5;
            }
            while (failsafe < 5 && sender.getServer().getWorlds().get(0).getBlockAt(pos).getType().toString().contains("WATER")) {
                x = (int) (Math.random() * sender.getServer().getMaxWorldSize() * 2) - sender.getServer().getMaxWorldSize();
                z = (int) (Math.random() * sender.getServer().getMaxWorldSize() * 2) - sender.getServer().getMaxWorldSize();
                pos = sender.getServer().getWorlds().get(0).getHighestBlockAt(x, z).getLocation();
                pos.add(0, 1, 0);
                failsafe++;
            }
            sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Teleporting to " + x + ", " + z);
            ((Player) sender).teleport(sender.getServer().getWorlds().get(0).getHighestBlockAt(x, z).getLocation());
            return true;
        } else {
            sender.sendMessage("You do not have permission to use this command!");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return List.of("unsafe");
        } else if (args.length == 1) {
            return List.of("unsafe");
        }
        return new LinkedList<>();
    }
}
