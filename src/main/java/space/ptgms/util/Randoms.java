package space.ptgms.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class Randoms implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return switch (command.getName()) {
            case "8ball" -> ask8ball(sender, args);
            case "calculator" -> calculator(sender, args);
            case "jumpscare" -> jumpscare(sender, args);
            default -> false;
        };
    }

    private boolean jumpscare(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ptgmsUtils.jumpscare")) {
            sender.sendMessage("You do not have permission to use this command!");
            return false;
        }

        Player player = sender.getServer().getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage("That player is not online or does not exist!");
            return false;
        }

        EntityEffect effect = null;

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("guardian")) {
                effect = EntityEffect.GUARDIAN_TARGET;
            } else if (args[1].equalsIgnoreCase("totem")) {
                effect = EntityEffect.TOTEM_RESURRECT;
            } else {
                sender.sendMessage("That is not a valid effect!");
                return false;
            }
        }

        assert effect != null;
        player.playEffect(effect);
        player.sendMessage(ChatColor.DARK_PURPLE + "You have been jumpscared by " + sender.getName() + "!");
        return true;
    }

    private boolean ask8ball(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("You must ask a question!");
            return false;
        }
        StringBuilder question = new StringBuilder();
        for (String arg : args) {
            question.append(arg).append(" ");
        }
        sender.sendMessage("You asked: " + question);
        sender.sendMessage(ChatColor.DARK_PURPLE + "The 8 ball says: " + ChatColor.BOLD + getAnswer());
        return true;
    }

    private boolean calculator(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("You must enter a Name!");
            return false;
        }
        sender.sendMessage(ChatColor.DARK_PURPLE + "You asked if you are compatible with: " + ChatColor.BOLD + args[0]);
        sender.sendMessage(ChatColor.DARK_PURPLE + "The calculator says: " + ChatColor.BOLD + getCompatibility(sender.getName(), args[0]));
        return true;
    }

    private static String getCompatibility(String name, String otherName) {
        int value = 40 - Math.abs(name.length() - otherName.length());
        int charCount = Math.min(name.length(), otherName.length());
        int difference = 0;

        int[] firstHalf = new int[2];
        int[] secondHalf = new int[2];

        for (int i = 0; i < Math.max(name.length(), otherName.length()); i++) {
            if (i < name.length()) {
                if (name.charAt(i) < 78) {
                    firstHalf[0]++;
                } else {
                    secondHalf[0]++;
                }
            }

            if (i < otherName.length()) {
                if (otherName.charAt(i) < 78) {
                    firstHalf[1]++;
                } else {
                    secondHalf[1]++;
                }
            }

            if (i < charCount && name.charAt(i) == otherName.charAt(i)) {
                difference++;
            }
        }

        if (difference != 0) {
            float floatDifference = (float) charCount / difference;
            value += (int) (floatDifference * 30);
        }

        int abs = Math.abs(secondHalf[0] - secondHalf[1]);
        if (abs != 0) {
            float alphabetDifference = (float) Math.abs(firstHalf[0] - firstHalf[1]) / abs;
            value += (int) (alphabetDifference * 10);
        }

        boolean firstCharMatches = name.charAt(0) == otherName.charAt(0);
        boolean lastCharMatches = name.charAt(name.length() - 1) == otherName.charAt(otherName.length() - 1);

        // check if either first characters match or last characters are both vowels or both consonants
        if (firstCharMatches || lastCharMatches && (name.charAt(name.length() - 1) < 78) == (otherName.charAt(otherName.length() - 1) < 78)) {
            value += 10;
        }

        // check if both names start or end with the same letter
        if (firstCharMatches || lastCharMatches) {
            value += 2;
        }

        // check if both names contain . , - or _
        if (name.contains(".") && otherName.contains(".") || name.contains(",") && otherName.contains(",") || name.contains("-") && otherName.contains("-") || name.contains("_") && otherName.contains("_")) {
            value += 2;
        }

//        get ascii values of both names chars, add them together, divide by each other, multiply by 5
        float asciiValue = getAsciiValue(name, otherName);

        value += (int) (asciiValue * 5);


        // out of niceness, add 1 for free
        value += 1;

        value = Math.max(0, value);

        if (value > 100) {
            value = 100 - (value - 100);
        }

        if (value < 0) {
            value = (value / 2) * -1;
        }

        return value + "%";
    }

    private static float getAsciiValue(String name, String otherName) {
        int asciiValueFirst = 0;
        int asciiValueSecond = 0;
        for (int i = 0; i < name.length(); i++) {
            asciiValueFirst += name.charAt(i);
        }

        for (int i = 0; i < otherName.length(); i++) {
            asciiValueSecond += otherName.charAt(i);
        }

        return (float) Math.min(asciiValueFirst, asciiValueSecond) / Math.max(asciiValueFirst, asciiValueSecond);
    }


    private String getAnswer() {
        int answer = (int) (Math.random() * 20);

        return switch (answer) {
            case 0 -> "It is certain.";
            case 1 -> "It is decidedly so.";
            case 2 -> "Without a doubt.";
            case 3 -> "Yes - definitely.";
            case 4 -> "You may rely on it.";
            case 5 -> "As I see it, yes.";
            case 6 -> "Most likely.";
            case 7 -> "Outlook good.";
            case 8 -> "Yes.";
            case 9 -> "Signs point to yes.";
            case 10 -> "Reply hazy, try again.";
            case 11 -> "Ask again later.";
            case 12 -> "Better not tell you now.";
            case 13 -> "Cannot predict now.";
            case 14 -> "Concentrate and ask again.";
            case 15 -> "Don't count on it.";
            case 16 -> "My reply is no.";
            case 17 -> "My sources say no.";
            case 18 -> "Outlook not so good.";
            case 19 -> "Very doubtful.";
            default -> "No.";
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if ("8ball".equals(command.getName())) {
            return new LinkedList<>();
        } else if ("calculator".equals(command.getName())) {
            return null;
        } else if ("jumpscare".equals(command.getName())) {
            if (args.length == 1) {
                List<String> players = new LinkedList<>();
                sender.getServer().getOnlinePlayers().forEach(player -> players.add(player.getName()));
                return players;
            } else if (args.length == 2) {
                return List.of("guardian", "totem");
            }
        }
        return null;
    }
}
