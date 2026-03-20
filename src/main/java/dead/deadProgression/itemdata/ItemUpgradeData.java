package dead.deadProgression.itemdata;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemUpgradeData {

/*

    This class will construct an instance from the PDC of an item.
    The PDC will contain the UUID of the progression data along with a list of upgrade data, as follows:
    ProgressionUUID1[UpgradeUUID1=Value1,UpgradeUUID2=Value2]ProgressionUUID2[UpgradeUUID3=Value3]


 */

    private Map<UUID, Map<UUID, Integer>> upgradeData = new HashMap<>();


    public ItemUpgradeData(Map<UUID, Map<UUID, Integer>> upgradeData) {
        if (upgradeData == null || upgradeData.isEmpty()) {
            return;
        }
        this.upgradeData = upgradeData;
    }

    public void addOrCreate(UUID progressionSetUUID, UUID upgradeUUID, int tier) {
        if (!upgradeData.containsKey(progressionSetUUID)) {
            upgradeData.put(progressionSetUUID, new HashMap<>());
        }
        upgradeData.get(progressionSetUUID).put(upgradeUUID, tier);
    }

    public int getTier(UUID progressionSetUUID, UUID upgradeUUID) {
        if (!upgradeData.containsKey(progressionSetUUID)) {
            return -1; // Safe to return -1 because clearly the tier is not set for this upgrade
        }
        Integer i = upgradeData.get(progressionSetUUID).get(upgradeUUID);
        if (i == null || i < 0) {
            return -1;
        }
        return i;
    }

    public boolean hasUpgrade(UUID progressionSetUUID, UUID upgradeUUID) {
        if (!upgradeData.containsKey(progressionSetUUID)) {
            return false;
        }
        return upgradeData.get(progressionSetUUID).containsKey(upgradeUUID);
    }

    public void removeUpgrade(UUID progressionSetUUID, UUID upgradeUUID) {
        if (!upgradeData.containsKey(progressionSetUUID)) {
            return;
        }
        upgradeData.get(progressionSetUUID).remove(upgradeUUID);
    }

    public void removeProgressionSet(UUID progressionSetUUID) {
        upgradeData.remove(progressionSetUUID);
    }

    public Map<UUID, Map<UUID, Integer>> getUpgradeData() {
        return upgradeData;
    }

    public boolean serialize() {

    }

    public static ItemUpgradeData deserialize(String persistentDataString) {

    }


}
