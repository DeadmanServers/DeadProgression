package dead.deadProgression.upgrades;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.ability.AbilityData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.*;

public class UpgradeRegistry {

    private Map<UUID, UpgradeData> upgrades = new HashMap<>();
    private final File file = new File(DeadProgression.INSTANCE.getDataFolder(), "data/upgradedata.yml");
    {
        file.getParentFile().mkdirs();
    }
    private final PoaYaml yml = PoaYaml.loadFromFile(file, true);

    public void load() {
        DeadProgression.INSTANCE.getLogger().info("Loading Upgrade Data...");
        if (yml.isConfigurationSection("UpgradeData")) {
            for (String data : yml.getConfigurationSection("UpgradeData").getKeys(false)) {
                try  {
                    UUID id = UUID.fromString(data);
                    if (exists(id)) {
                        continue;
                    }
                    String ymlVar = "UpgradeData." + data;

                    String stringID = yml.getString(ymlVar + ".AbilityID");
                    UUID abilityID = UUID.fromString(stringID);
                    String name = yml.getString(ymlVar + ".Name");
                    List<Material> materials = new ArrayList<>();
                    Map<Integer, Double> valuesPerTier = new HashMap<>();
                    Map<Integer, List<ItemStack>> pricePerTier = new HashMap<>();
                    for (String stringMaterial : yml.getStringList(ymlVar + ".Materials")) {
                        Material material = Material.getMaterial(stringMaterial);
                        if (material == null) {
                            continue;
                        }
                        materials.add(material);
                    }
                    for (String key : yml.getConfigurationSection(ymlVar + ".ValuesPerTier").getKeys(false)) {
                        valuesPerTier.put(Integer.parseInt(key), yml.getDouble(ymlVar + ".ValuesPerTier." + key));
                    }
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

                    register(id, abilityID, name, materials, valuesPerTier, pricePerTier);
                } catch (Exception e) {
                    DeadProgression.INSTANCE.getLogger().warning("Failed to load Upgrade Data: " + data);
                    continue;
                }
            }
        }
        DeadProgression.INSTANCE.getLogger().info("Completed Loading Upgrade Data!");
    }

    public void register(UUID upgradeID, UUID abilityID, String name, List<Material> materials, Map<Integer, Double> valuesPerTier, Map<Integer, List<ItemStack>> pricePerTier) {
        UpgradeData data = new UpgradeData(upgradeID, abilityID, name);
        data.setAllowedItems(materials);
        data.setValuesPerTier(valuesPerTier);
        data.setPricePerTier(pricePerTier);
        upgrades.put(upgradeID, data);
    }

    public void save() {
        DeadProgression.INSTANCE.getLogger().info("Saving Upgrade Data...");
        for (UUID id : upgrades.keySet()) {
            UpgradeData data = upgrades.get(id);
            if (data == null) {
                DeadProgression.INSTANCE.getLogger().warning("Failed to save Upgrade Data: " + id);
                continue;
            }

            String ymlVar = "UpgradeData." + id;
            UUID abilityID = data.getAbilityID();
            String name = data.getName();
            List<Material> materials = data.getAllowedItems();
            Map<Integer, Double> valuesPerTier = data.getValuesPerTier();
            Map<Integer, List<ItemStack>> pricePerTier = data.getPricePerTier();

            if (name == null) {
                DeadProgression.INSTANCE.getLogger().warning("Failed to save name for Upgrade Data: " + id);
            } else {
                yml.set(ymlVar + ".Name", name);
            }
            if (abilityID == null) {
                DeadProgression.INSTANCE.getLogger().warning("Failed to save AbilityID for Upgrade Data: " + id);
            } else {
                yml.set(ymlVar + ".AbilityID", abilityID.toString());
            }
            if (materials == null) {
                yml.set(ymlVar + ".Materials", null);
            } else {
                yml.set(ymlVar + ".Materials", materials.toString());
            }
            if (valuesPerTier != null && !valuesPerTier.isEmpty()) {
                for (Integer i : valuesPerTier.keySet()) {
                    double value = valuesPerTier.get(i);
                    yml.set(ymlVar + ".ValuesPerTier." + i, value);
                }
            }
            if (pricePerTier != null && !pricePerTier.isEmpty()) {
                for (Integer i : pricePerTier.keySet()) {
                    List<ItemStack> price = pricePerTier.get(i);
                    int counter = 1;
                    for (ItemStack item : price) {
                        ItemStack clone = item.clone();
                        yml.setItemStack(ymlVar + ".PricePerTier." + i + counter, clone);
                        counter++;
                    }
                }
            }
        }
        yml.saveAsync(file);
        DeadProgression.INSTANCE.getLogger().info("Completed Saving Upgrade Data!");
    }

    public boolean exists(UUID id) {
        return upgrades.containsKey(id);
    }

}
