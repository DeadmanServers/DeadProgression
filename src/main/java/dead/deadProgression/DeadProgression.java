package dead.deadProgression;

import dead.deadProgression.ability.AbilityRegistry;
import dead.deadProgression.commands.ProgressionDebugCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeadProgression extends JavaPlugin {

    public static DeadProgression INSTANCE;
    public static AbilityRegistry abilityRegistry;
    
    @Override
    public void onEnable() {
        INSTANCE = this;
        abilityRegistry = new AbilityRegistry();

        saveDefaultConfig();
        abilityRegistry.load();

        getCommand("upgradedebug").setExecutor(new ProgressionDebugCommand());

    }

    @Override
    public void onDisable() {
        if (abilityRegistry != null) {
            abilityRegistry.save();
        }
    }
}
