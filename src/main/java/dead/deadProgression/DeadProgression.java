package dead.deadProgression;

import dead.deadProgression.commands.ProgressionDebugCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeadProgression extends JavaPlugin {

    private static DeadProgression INSTANCE;
    
    @Override
    public void onEnable() {
        INSTANCE = this;

        saveDefaultConfig();

        getCommand("upgradedebug").setExecutor(new ProgressionDebugCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
