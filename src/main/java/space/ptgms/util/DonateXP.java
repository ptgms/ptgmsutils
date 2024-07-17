package space.ptgms.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DonateXP implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("donatexp.use")) {
            player.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("Usage: /donatexp <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("Player not found.");
            return true;
        }

        // check if arg ends in l for levels
        if (args[1].endsWith("l")) {
            int levels;
            try {
                levels = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid XP amount. Please enter a number.");
                return true;
            }

            if (levels <= 0) {
                player.sendMessage("XP amount must be positive.");
                return true;
            }

            int playerLevels = player.getLevel();
            if (playerLevels < levels) {
                player.sendMessage("You don't have enough XP levels. Your current levels: " + playerLevels);
                return true;
            }

            player.setLevel(playerLevels - levels);
            target.setLevel(target.getLevel() + levels);

            player.sendMessage("You've sent " + levels + " XP levels to " + target.getName() + ".");
            target.sendMessage("You've received " + levels + " XP levels from " + player.getName() + ".");

            return true;
        } else {
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid XP amount. Please enter a number.");
                return true;
            }

            if (amount <= 0) {
                player.sendMessage("XP amount must be positive.");
                return true;
            }

            int playerXP = player.getTotalExperience();
            if (playerXP < amount) {
                player.sendMessage("You don't have enough XP. Your current XP: " + playerXP);
                return true;
            }

            player.giveExp(-amount);
            target.giveExp(amount);

            player.sendMessage("You've sent " + amount + " XP to " + target.getName() + ".");
            target.sendMessage("You've received " + amount + " XP from " + player.getName() + ".");

            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return List.of("5", "10", "15", "20", "25", "30");
        }
        return new ArrayList<>();
    }
}