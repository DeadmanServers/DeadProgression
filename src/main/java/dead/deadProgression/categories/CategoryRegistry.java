package dead.deadProgression.categories;

import dead.deadProgression.DeadProgression;
import org.bukkit.entity.Cat;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.*;

public class CategoryRegistry {

    private Map<UUID, CategoryData> categoryData = new HashMap<>();
    private final File file = new File(DeadProgression.INSTANCE.getDataFolder(), "data/categorydata.yml");
    {
        file.getParentFile().mkdirs();
    }
    private final PoaYaml yml = PoaYaml.loadFromFile(file, true);

    public void load() {
        DeadProgression.INSTANCE.getLogger().info("Loading Category Data...");
        if (yml.isConfigurationSection("CategoryData")) {
            for (String data : yml.getConfigurationSection("CategoryData").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(data);
                    if (exists(id)) {
                        continue;
                    }
                    String ymlVar = "AbilityData." + data;
                    String name = yml.getString(ymlVar + ".Name");
                    List<UUID> upgrades = new ArrayList<>();
                    for (String key : yml.getConfigurationSection(ymlVar + ".Upgrades").getKeys(false)) {
                        UUID upgradeID = UUID.fromString(key);
                        upgrades.add(upgradeID);
                    }
                    register(id, name, upgrades);
                } catch (Exception e) {
                    DeadProgression.INSTANCE.getLogger().warning("Failed to load Category Data: " + data);
                    continue;
                }
            }
        }
        DeadProgression.INSTANCE.getLogger().info("Completed loading Category Data!");
    }

    public void save() {
        DeadProgression.INSTANCE.getLogger().info("Saving Category Data...");
        for (UUID id : categoryData.keySet()) {
            CategoryData data = categoryData.get(id);

            if (data == null) {
                DeadProgression.INSTANCE.getLogger().warning("Category Data id not found! " + id);
                continue;
            }

            String ymlVar = "CategoryData." + id;
            String name = data.getName();
            List<UUID> upgrades = new ArrayList<>(data.getUpgrades());
            if (name == null) {
                DeadProgression.INSTANCE.getLogger().warning("Category Data name not found! This category may not load properly. " + id);
            } else {
                yml.set(ymlVar + ".Name", name);
            }
            if (upgrades.isEmpty()) {
                yml.set(ymlVar + ".Upgrades", null);
            } else {
                yml.set(ymlVar + ".Upgrades", upgrades);
            }
            yml.saveAsync(file);
            DeadProgression.INSTANCE.getLogger().info("Saved Category Data!");
        }
    }

    public void register(UUID id, String name, List<UUID> upgrades) {
        CategoryData data = new CategoryData(id, name);
        data.setUpgrades(upgrades);
        this.categoryData.put(id, data);
    }

    public CategoryData get(UUID id) {
        return this.categoryData.get(id);
    }
    public CategoryData get(String name) {
        for (UUID id : categoryData.keySet()) {
            CategoryData data = categoryData.get(id);
            if (data.getName().equalsIgnoreCase(name)) {
                return data;
            }
        }
        return null;
    }

    public List<CategoryData> getAll() {
        return new ArrayList<>(this.categoryData.values());
    }

    public List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        for (UUID id : categoryData.keySet()) {
            CategoryData data = categoryData.get(id);
            names.add(data.getName());
        }
        return names;
    }

    public boolean exists(UUID id) {
        return categoryData.containsKey(id);
    }
    public boolean exists(String name) {
        for (UUID id : categoryData.keySet()) {
            CategoryData data = categoryData.get(id);
            if (data.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void unregister(String name) {
        for (UUID id : categoryData.keySet()) {
            CategoryData data = categoryData.get(id);
            if (data.getName().equalsIgnoreCase(name)) {
                categoryData.remove(id);
            }
        }
    }

    public void unregister(UUID id) {
        this.categoryData.remove(id);
    }

    public void unregister(CategoryData data) {
        this.categoryData.remove(data.getId());
    }


}
