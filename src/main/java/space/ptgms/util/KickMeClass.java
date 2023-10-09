package space.ptgms.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class KickMeClass implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You do know you can just /stop the server, right?");
            return false;
        }
        sender.getServer().broadcastMessage(sender.getName() + " feels very dramatic today.");
        Objects.requireNonNull(sender.getServer().getPlayer(sender.getName())).kickPlayer("You kicked yourself!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new LinkedList<>();
    }
}
