package dead.deadProgression;

import dead.deadProgression.ability.AbilityRegistry;
import dead.deadProgression.categories.CategoryRegistry;
import dead.deadProgression.commands.ProgressionDebugCommand;
import dead.deadProgression.menu.MenuListener;
import dead.deadProgression.progression.ProgressionRegistry;
import dead.deadProgression.upgrades.UpgradeRegistry;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeadProgression extends JavaPlugin {

    public static DeadProgression INSTANCE;
    public static AbilityRegistry abilityRegistry;
    public static UpgradeRegistry upgradeRegistry;
    public static CategoryRegistry categoryRegistry;
    public static ProgressionRegistry progressionRegistry;

    @Override
    public void onEnable() {
        INSTANCE = this;
        abilityRegistry = new AbilityRegistry();
        upgradeRegistry = new UpgradeRegistry();
        categoryRegistry = new CategoryRegistry();
        progressionRegistry = new ProgressionRegistry();

        saveDefaultConfig();
        abilityRegistry.load();
        upgradeRegistry.load();
        categoryRegistry.load();
        progressionRegistry.load();

        getCommand("upgradedebug").setExecutor(new ProgressionDebugCommand());
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MenuListener(), this);

    }

    @Override
    public void onDisable() {
        if (abilityRegistry != null) {
            abilityRegistry.save();
        }
        if (upgradeRegistry != null) {
            upgradeRegistry.save();
        }
        if (categoryRegistry != null) {
            categoryRegistry.save();
        }
        if (progressionRegistry != null) {
            progressionRegistry.save();
        }
    }
}
