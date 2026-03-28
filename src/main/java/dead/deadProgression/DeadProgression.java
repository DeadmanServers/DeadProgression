package dead.deadProgression;

import dead.deadProgression.data.AbilityData;
import dead.deadProgression.data.CategoryData;
import dead.deadProgression.chatinputmanager.ChatInputListener;
import dead.deadProgression.commands.ProgressionDebugCommand;
import dead.deadProgression.data.ProgressionData;
import dead.deadProgression.menu.MenuListener;
import dead.deadProgression.data.UpgradeData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeadProgression extends JavaPlugin {

    public static DeadProgression INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        saveDefaultConfig();
        AbilityData.load();
        UpgradeData.load();
        CategoryData.load();
        ProgressionData.load();

        getCommand("upgradedebug").setExecutor(new ProgressionDebugCommand());
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new ChatInputListener(), this);

    }

    @Override
    public void onDisable() {
    }
}
