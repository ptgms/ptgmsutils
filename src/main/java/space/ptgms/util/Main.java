package space.ptgms.util;

import java.util.List;
import java.util.Objects;

import org.bukkit.plugin.java.JavaPlugin;
import space.ptgms.util.data.Plugins;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        List<Plugins> commands = List.of(
                new Plugins(Objects.requireNonNull(this.getCommand("wild")), new TPClass(), new TPClass()),
                new Plugins(Objects.requireNonNull(this.getCommand("kickme")), new KickMeClass(), new KickMeClass()),
                new Plugins(Objects.requireNonNull(this.getCommand("8ball")), new Randoms(), new Randoms()),
                new Plugins(Objects.requireNonNull(this.getCommand("calculator")), new Randoms(), new Randoms()),
                new Plugins(Objects.requireNonNull(this.getCommand("jumpscare")), new Randoms(), new Randoms()),
                new Plugins(Objects.requireNonNull(this.getCommand("spawnlimit")), new SpawnLimit(), new SpawnLimit())
        );

        for (Plugins plugin : commands) {
            plugin.plugin.setExecutor(plugin.executor);
            plugin.plugin.setTabCompleter(plugin.autoComplete);
        }

        new PlayerController(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
