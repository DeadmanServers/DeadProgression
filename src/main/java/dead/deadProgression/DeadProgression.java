package dead.deadProgression;

import dead.deadProgression.ability.AbilityData;
import dead.deadProgression.categories.CategoryRegistry;
import dead.deadProgression.chatinputmanager.ChatInputListener;
import dead.deadProgression.commands.ProgressionDebugCommand;
import dead.deadProgression.menu.MenuListener;
import dead.deadProgression.progression.ProgressionRegistry;
import dead.deadProgression.upgrades.UpgradeData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeadProgression extends JavaPlugin {

    public static DeadProgression INSTANCE;
    public static CategoryRegistry categoryRegistry;
    public static ProgressionRegistry progressionRegistry;

    @Override
    public void onEnable() {
        INSTANCE = this;
        categoryRegistry = new CategoryRegistry();
        progressionRegistry = new ProgressionRegistry();

        saveDefaultConfig();
        AbilityData.load();
        UpgradeData.load();
        categoryRegistry.load();
        progressionRegistry.load();

        getCommand("upgradedebug").setExecutor(new ProgressionDebugCommand());
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new ChatInputListener(), this);

    }

    @Override
    public void onDisable() {
        if (categoryRegistry != null) {
            categoryRegistry.save();
        }
        if (progressionRegistry != null) {
            progressionRegistry.save();
        }
    }
}
