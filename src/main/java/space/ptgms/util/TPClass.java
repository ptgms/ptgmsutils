package space.ptgms.util;

import java.util.Arrays;
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
            int x = (int) (Math.random() * (sender.getServer().getMaxWorldSize() / 2) * 2) - (sender.getServer().getMaxWorldSize() / 2);
            int z = (int) (Math.random() * (sender.getServer().getMaxWorldSize() / 2) - (sender.getServer().getMaxWorldSize() / 2));
            Location pos = sender.getServer().getWorlds().get(0).getHighestBlockAt(x, z).getLocation();
            pos.add(0, 1, 0);
            int world = 0;
//            check if its in water
            int failsafe = 0;
            int maxFail = 5;
            if (Arrays.asList(args).contains("unsafe")) {
                // user specified they are fine with water
                failsafe = maxFail;
            }
            if (Arrays.asList(args).contains("end")) {
                maxFail = 10;
                world = 2;
            }
            while (failsafe < maxFail && sender.getServer().getWorlds().get(world).getBlockAt(pos).getType().toString().contains("WATER") || sender.getServer().getWorlds().get(world).getBlockAt(pos).getType().toString().contains("LAVA") || sender.getServer().getWorlds().get(world).getBlockAt(pos).getType().toString().contains("AIR")) {
                x = (int) (Math.random() * sender.getServer().getMaxWorldSize() * 2) - sender.getServer().getMaxWorldSize();
                z = (int) (Math.random() * sender.getServer().getMaxWorldSize() * 2) - sender.getServer().getMaxWorldSize();
                pos = sender.getServer().getWorlds().get(world).getHighestBlockAt(x, z).getLocation();
                if (world == 2 && pos.getY() > 20) {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Didn't find a safe position to teleport to! Try again");
                    return true;
                }
                failsafe++;
            }
            pos = pos.add(0, 1, 0);
            sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Teleporting to " + x + ", " + z);
            ((Player) sender).teleport(pos);
            return true;
        } else {
            sender.sendMessage("You do not have permission to use this command!");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        LinkedList<String> list = new LinkedList<>();
        if (!Arrays.asList(args).contains("unsafe")) {
            list.add("unsafe");
        }
        if (!Arrays.asList(args).contains("end")) {
            list.add("end");
        }
        return list;
    }
}
