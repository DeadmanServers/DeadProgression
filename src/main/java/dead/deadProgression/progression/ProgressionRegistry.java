package dead.deadProgression.progression;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.categories.CategoryData;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.*;

public class ProgressionRegistry {

    private Map<UUID, ProgressionData> progressionData = new HashMap<>();
    private final File file = new File(DeadProgression.INSTANCE.getDataFolder(), "data/progressiondata.yml");
    {
        file.getParentFile().mkdirs();
    }
    private final PoaYaml yml = PoaYaml.loadFromFile(file, true);

    public void load() {
        DeadProgression.INSTANCE.getLogger().info("Loading Progression Data...");
        if (yml.isConfigurationSection("ProgressionData")) {
            for (String data : yml.getConfigurationSection("ProgressionData").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(data);
                    if (exists(id)) {
                        continue;
                    }
                    String ymlVar = "ProgressionData." + data;
                    String name = yml.getString(ymlVar + ".Name");
                    List<UUID> categories = new ArrayList<>();
                    for (String key : yml.getStringList(ymlVar + ".Categories")) {
                        UUID categoryID = UUID.fromString(key);
                        categories.add(categoryID);
                    }
                    register(id, name, categories);
                } catch (Exception e) {
                    DeadProgression.INSTANCE.getLogger().warning("Failed to load ProgressionData: " + e);
                    continue;
                }
            }
        }
        DeadProgression.INSTANCE.getLogger().info("Completed loading Progression Data!");
    }

    public void save() {
        DeadProgression.INSTANCE.getLogger().info("Saving Progression Data...");
        for (UUID id : progressionData.keySet()) {
            ProgressionData data = progressionData.get(id);

            if (data == null) {
                DeadProgression.INSTANCE.getLogger().warning("Failed to load ProgressionData: " + id);
                continue;
            }

            String ymlVar = "ProgressionData." + id;
            String name = data.getName();
            if (name == null) {
                DeadProgression.INSTANCE.getLogger().warning("Progression Data name not found! This progression may not load properly. " + id);
            } else {
                yml.set(ymlVar + ".Name", name);
            }
            if (data.getCategories().isEmpty()) {
                yml.set(ymlVar + ".Categories", null);
            } else {
                yml.set(ymlVar + ".Categories", data.getCategories());
            }
        }
        DeadProgression.INSTANCE.getLogger().info("Completed Saving Progression Data!");
        yml.saveAsync(file);
    }

    public void register(UUID id, String name, List<UUID> categories) {
        progressionData.put(id, new ProgressionData(id, name, categories));
    }

    public ProgressionData get(UUID id) {
        return progressionData.get(id);
    }
    public ProgressionData get(String name) {
        for (UUID id : progressionData.keySet()) {
            if (progressionData.get(id).getName().equals(name)) {
                return progressionData.get(id);
            }
        }
        return null;
    }

    public List<String> getAllNames() {
        List<String> list = new ArrayList<>();
        for (UUID id : progressionData.keySet()) {
            list.add(progressionData.get(id).getName());
        }
        return list;
    }

    public boolean exists(UUID id) {
        return progressionData.containsKey(id);
    }
    public boolean exists(String name) {
        return get(name) != null;
    }

    public void unregister(UUID id) {
        progressionData.remove(id);
    }
    public void unregister(CategoryData data) {
        progressionData.remove(data.getId());
    }
}
