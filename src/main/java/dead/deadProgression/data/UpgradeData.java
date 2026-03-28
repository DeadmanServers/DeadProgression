package dead.deadProgression.data;

import dead.deadProgression.DeadProgression;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.*;

public class UpgradeData {

    public static final Map<UUID, UpgradeData> upgradeData = new HashMap<>();

    private static final File file = new File(DeadProgression.INSTANCE.getDataFolder(), "data/upgradedata.yml");
    static {
        file.getParentFile().mkdirs();
    }

    private static final PoaYaml yml = new PoaYaml().loadFromFile(file, true);

    public static void load() {
        if (yml.isConfigurationSection("UpgradeData")) {
            for (String upgrade : yml.getConfigurationSection("UpgradeData").getKeys(false)) {
                try {
                    UUID upgradeID = UUID.fromString(upgrade);
                    if (upgradeData.containsKey(upgradeID)) {
                        continue;
                    }
                    UpgradeData data = new UpgradeData(upgradeID);
                    upgradeData.put(upgradeID, data);
                } catch (Exception e) {
                    DeadProgression.INSTANCE.getLogger().warning("Failed to load upgrade data: " + upgrade);
                    continue;
                }
            }
        }
    }

    private final UUID upgradeID;
    private String upgradeName;
    private int maxTiers;
    private UUID abilityID;
    private List<Material> allowedItems = new ArrayList<>();
    private Map<Integer, Double> valuesPerTier = new HashMap<>();
    private Map<Integer, List<ItemStack>> pricePerTier = new HashMap<>();
    private final String ymlVar;

    public UpgradeData(UUID upgradeID) {
        this.upgradeID = upgradeID;
        ymlVar = "UpgradeData." + upgradeID;
        // Creating a new Upgrade (Consider creating default values in config.yml)
        if (!yml.isSet(ymlVar)) {
            yml.set(ymlVar + ".Name", "New Upgrade");
            yml.set(ymlVar + ".MaxTiers", 0);
            yml.set(ymlVar + ".AbilityID", null);
            yml.set(ymlVar + ".AllowedItems", null);
            yml.set(ymlVar + ".ValuesPerTier", null);
            yml.set(ymlVar + ".PricePerTier", null);

            yml.saveAsync(file);
        }

        // Getting created values
        if (yml.isSet(ymlVar + ".Name")) {
            upgradeName = yml.getString(ymlVar + ".Name");
        }
        if (yml.isSet(ymlVar + ".MaxTiers")) {
            maxTiers = yml.getInt(ymlVar + ".MaxTiers");
        }
        if (yml.isSet(ymlVar + ".AbilityID")) {
            String idFetch = yml.getString(ymlVar + ".AbilityID");
            if (idFetch != null && !idFetch.isBlank()) {
                abilityID = UUID.fromString(idFetch);
            }
        }
        if (yml.isSet(ymlVar + ".AllowedItems")) {
            for (String stringMaterial : yml.getStringList(ymlVar + ".AllowedItems")) {
                Material material = Material.getMaterial(stringMaterial);
                if (material == null || material == Material.AIR) {
                    continue;
                }
                this.allowedItems.add(material);
            }
        }
        if (yml.isSet(ymlVar + ".PricePerTier")) {
            for (String key : yml.getConfigurationSection(ymlVar + ".PricePerTier").getKeys(false)) {
                List<ItemStack> priceList = new ArrayList<>();
                for (String stringNum : yml.getConfigurationSection(ymlVar + ".PricePerTier." + key).getKeys(false)) {
                    ItemStack itemStack = yml.getItemStack(ymlVar + ".PricePerTier." + key + "." + stringNum);
                    if (itemStack == null) {
                        continue;
                    }
                    priceList.add(itemStack);
                }
                pricePerTier.put(Integer.parseInt(key), priceList);
            }
        }
        if (yml.isSet(ymlVar + ".ValuesPerTier")) {
            for (String key : yml.getConfigurationSection(ymlVar + ".ValuesPerTier").getKeys(false)) {
                valuesPerTier.put(Integer.parseInt(key), yml.getDouble(ymlVar + ".ValuesPerTier." + key));
            }
        }

        upgradeData.put(upgradeID, this);
    }

    public UUID getId() {
        return upgradeID;
    }
    public UUID getAbilityID() {
        return abilityID;
    }
    public String getName() {
        return upgradeName;
    }
    public int getMaxTiers() { return maxTiers; }
    public List<Material> getAllowedItems() {
        return allowedItems;
    }
    public Map<Integer, Double> getValuesPerTier() {
        return valuesPerTier;
    }
    public Map<Integer, List<ItemStack>> getPricePerTier() {
        return pricePerTier;
    }
    public void setAbilityID(UUID abilityID) {
        this.abilityID = abilityID;

        yml.set(ymlVar + ".AbilityID", abilityID.toString());
        yml.saveAsync(file);
    }
    public void setName(String name) {
        this.upgradeName = name;

        yml.set(ymlVar + ".Name", name);
        yml.saveAsync(file);
    }
    public void setMaxTiers(int maxTiers) {
        this.maxTiers = maxTiers;

        yml.set(ymlVar + ".MaxTiers", maxTiers);
        yml.saveAsync(file);
    }
    public void setAllowedItems(List<Material> allowedItems) {
        this.allowedItems = allowedItems;

        yml.set(ymlVar + ".AllowedItems", allowedItems);
        yml.saveAsync(file);
    }
    public void addAllowedItem(Material material) {
        this.allowedItems.add(material);

        yml.addStringList(ymlVar + ".AllowedItems", material.name());
        yml.saveAsync(file);
    }
    public void removeAllowedItem(Material material) {
        this.allowedItems.remove(material);

        yml.removeStringList(ymlVar + ".AllowedItems", material.name());
        yml.saveAsync(file);
    }
    public void setValuesPerTier(Map<Integer, Double> valuesPerTier) {
        this.valuesPerTier = valuesPerTier;

        for (Integer i : valuesPerTier.keySet()) {
            double value = valuesPerTier.get(i);
            yml.set(ymlVar + ".ValuesPerTier." + i, value);
        }
        yml.saveAsync(file);
    }
    public void addValuePerTier(int tier, double value) {
        this.valuesPerTier.put(tier, value);

        yml.set(ymlVar + ".ValuesPerTier." + tier, value);
        yml.saveAsync(file);
    }
    public void setValueForTier(int tier, double value) {
        this.valuesPerTier.put(tier, value);

        yml.set(ymlVar + ".ValuesPerTier." + tier, value);
        yml.saveAsync(file);
    }
    public void clearLastValue() {
        int i = valuesPerTier.size() - 1;
        this.valuesPerTier.remove(i);

        yml.set(ymlVar + ".ValuesPerTier." + i, null);
        yml.saveAsync(file);
    }
    public void setPricePerTier(Map<Integer, List<ItemStack>> pricePerTier) {
        this.pricePerTier = pricePerTier;

        for (Integer i : pricePerTier.keySet()) {
            List<ItemStack> price = pricePerTier.get(i);
            int counter = 1;
            for (ItemStack item : price) {
                ItemStack clone = item.clone();
                yml.setItemStack(ymlVar + ".PricePerTier." + i + counter, clone);
                counter++;
            }
        }
        yml.saveAsync(file);
    }
    public void addPricePerTier(int tier, List<ItemStack> items) {
        this.pricePerTier.put(tier, items);

        int counter = 1;
        for (ItemStack item : items) {
            ItemStack clone = item.clone();
            yml.setItemStack(ymlVar + ".PricePerTier." + tier + counter, clone);
            counter++;
        }
        yml.saveAsync(file);
    }
    public void setPriceForTier(int tier, List<ItemStack> items) {
        this.pricePerTier.put(tier, items);

        int counter = 1;
        for (ItemStack item : items) {
            ItemStack clone = item.clone();
            yml.setItemStack(ymlVar + ".PricePerTier." + tier + counter, clone);
            counter++;
        }
        yml.saveAsync(file);
    }
    public void clearLastPrice() {
        int i = pricePerTier.size() - 1;
        this.pricePerTier.remove(i);

        yml.set(ymlVar + ".PricePerTier." + i, null);
        yml.saveAsync(file);
    }

    public static List<UpgradeData> getAllUpgrades() {
        return new ArrayList<>(upgradeData.values());
    }

    public static UpgradeData getUpgrade(UUID upgradeID) {
        return upgradeData.get(upgradeID);
    }




}
