package cc.funkemunky.bungee;

import cc.funkemunky.api.commands.ancmd.CommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class IrisBungee extends JavaPlugin {
    private CommandManager commandManager;
    @Getter
    private static IrisBungee instance;

    public void onEnable() {
        instance = this;
        commandManager = new CommandManager(this);
    }
}
