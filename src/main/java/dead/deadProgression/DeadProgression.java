package dead.deadProgression;

import dead.deadProgression.ability.AbilityRegistry;
import dead.deadProgression.categories.CategoryRegistry;
import dead.deadProgression.commands.ProgressionDebugCommand;
import dead.deadProgression.upgrades.UpgradeRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeadProgression extends JavaPlugin {

    public static DeadProgression INSTANCE;
    public static AbilityRegistry abilityRegistry;
    public static UpgradeRegistry upgradeRegistry;
    public static CategoryRegistry categoryRegistry;

    @Override
    public void onEnable() {
        INSTANCE = this;
        abilityRegistry = new AbilityRegistry();
        upgradeRegistry = new UpgradeRegistry();
        categoryRegistry = new CategoryRegistry();

        saveDefaultConfig();
        abilityRegistry.load();
        upgradeRegistry.load();
        categoryRegistry.load();

        getCommand("upgradedebug").setExecutor(new ProgressionDebugCommand());

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
    }
}
