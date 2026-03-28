package dead.deadProgression.data;

import dead.deadProgression.DeadProgression;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
    private static final NamespacedKey KEY = new NamespacedKey(DeadProgression.INSTANCE, "upgrade_data");


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

    public String serialize() {

        StringBuilder builder = new StringBuilder();
        for (UUID uuid : upgradeData.keySet()) {
            builder.append(uuid.toString());
            builder.append("[");
            boolean isFirst = true;
            for (Map.Entry<UUID, Integer> entry : upgradeData.get(uuid).entrySet()) {
                if (!isFirst) {
                    builder.append(",");
                }
                builder.append(entry.getKey().toString());
                builder.append("=");
                builder.append(entry.getValue());
                isFirst = false;
            }
            builder.append("]");
        }
        return builder.toString();
    }

    // ProgressionSetUUID[UpgradeUUID=Tier,UpgradeUUID=Tier]ProgressionSetUUID[UpgradeUUID=tier]
    public static ItemUpgradeData deserialize(String persistentDataString) {

        if (persistentDataString == null || persistentDataString.isEmpty()) {
            return null;
        }

        Map<UUID, Map<UUID, Integer>> upgradeData = new HashMap<>();

        String[] progressionSets = persistentDataString.split("]");
        for (String progressionSet : progressionSets) {
            if (progressionSet == null || progressionSet.isEmpty()) {
                continue;
            }
            int open =  progressionSet.indexOf('[');
            if (open == -1) {
                continue;
            }
            String setStringID = progressionSet.substring(0, open).trim();
            String upgradeString = progressionSet.substring(open + 1).trim();
            try {
                UUID setUUID = UUID.fromString(setStringID);
                Map<UUID, Integer> upgradeMap = new HashMap<>();
                if (upgradeString.isBlank()) {
                    continue;
                }
                String[] upgradeStrings = upgradeString.split(",");
                for (String upgrade : upgradeStrings) {
                    if (upgrade.isBlank()) {
                        continue;
                    }
                    String[] upgradeWithTier = upgrade.split("=", 2);
                    if (upgradeWithTier.length != 2) {
                        continue;
                    }

                    try {
                        UUID upgradeUUID = UUID.fromString(upgradeWithTier[0].trim());
                        int tier = Integer.parseInt(upgradeWithTier[1].trim());
                        upgradeMap.put(upgradeUUID, tier);
                    } catch (IllegalArgumentException e) {
                        continue;
                    }

                }
                upgradeData.put(setUUID, upgradeMap);
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        return new ItemUpgradeData(upgradeData);
    }


    public static ItemUpgradeData fromItem(ItemStack item) {
        ItemStack clone = item.clone();
        if (clone.getType() == Material.AIR || clone.isEmpty()) {
            return null;
        }
        ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        String pdcString = pdc.get(KEY, PersistentDataType.STRING);
        if (pdcString == null || pdcString.isEmpty()) {
            return null;
        }
        return deserialize(pdcString);
    }

    public static void applyToItem(ItemStack item, ItemUpgradeData data) {
        if (item.getType() == Material.AIR || item.isEmpty()) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(KEY, PersistentDataType.STRING, data.serialize());
        item.setItemMeta(itemMeta);
    }


}
