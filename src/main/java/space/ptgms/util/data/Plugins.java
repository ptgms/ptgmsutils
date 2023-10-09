package space.ptgms.util.data;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class Plugins{
    public PluginCommand plugin;
    public CommandExecutor executor;
    public TabCompleter autoComplete;

    public Plugins(PluginCommand plugin, CommandExecutor executor, TabCompleter autoComplete){
        this.plugin = plugin;
        this.executor = executor;
        this.autoComplete = autoComplete;
    }
}