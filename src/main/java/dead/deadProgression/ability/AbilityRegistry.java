package dead.deadProgression.ability;

import dead.deadProgression.DeadProgression;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.*;

public class AbilityRegistry {

    private Map<UUID, AbilityData> abilityData = new HashMap<>();
    private final File file = new File(DeadProgression.INSTANCE.getDataFolder(), "data/abilitydata.yml");
    {
        file.getParentFile().mkdirs();
    }
    private final PoaYaml yml = PoaYaml.loadFromFile(file, true);


    public void load() {
        DeadProgression.INSTANCE.getLogger().info("Loading Ability Data...");
        if (yml.isConfigurationSection("AbilityData")) {
            for (String data : yml.getConfigurationSection("AbilityData").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(data);
                    if (exists(id)){
                        continue;
                    }
                    String ymlVar = "AbilityData." + data;

                    String typeString = yml.getString(ymlVar + ".Type");
                    if (typeString == null) {
                        DeadProgression.INSTANCE.getLogger().warning("AbilityData type not found! " + data);
                        continue;
                    }
                    AbilityType type = AbilityType.valueOf(typeString);
                    String name = yml.getString(ymlVar + ".Name");
                    if (name == null) {
                        DeadProgression.INSTANCE.getLogger().warning("AbilityData name not found! " + data);
                        continue;
                    }
                    List<String> description = yml.getStringList(ymlVar + ".Description");
                    double value = yml.getDouble(ymlVar + ".Value");


                    register(id, new AbilityData(id, name, type, description, value));
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }
        }
        DeadProgression.INSTANCE.getLogger().info("Completed Loading Ability Data!");
    }

    public void save() {
        DeadProgression.INSTANCE.getLogger().info("Saving AbilityData...");
        for (UUID id : abilityData.keySet()) {
            AbilityData data = abilityData.get(id);

            if (data == null) {
                DeadProgression.INSTANCE.getLogger().warning("AbilityData id not found! " + id);
                continue;
            }

            String ymlVar = "AbilityData." + id;
            String name = data.getName();
            AbilityType type = data.getType();
            List<String> description = data.getDescription();
            double value = data.getValue();

            if (name == null) {
                DeadProgression.INSTANCE.getLogger().warning("AbilityData name not found! This ability may not load properly." + id);
            } else {
                yml.set(ymlVar + ".Name", name);
            }
            if (type == null) {
                DeadProgression.INSTANCE.getLogger().warning("AbilityData type not found! Disabling this ability." + id);
                type = AbilityType.NO_VALUE;
            }
            yml.set(ymlVar + ".Type", type.toString());
            yml.set(ymlVar + ".Description", description);
            yml.set(ymlVar + ".Value", value);
        }
        yml.saveAsync(file);
        DeadProgression.INSTANCE.getLogger().info("Completed Saving AbilityData!");
    }

    public void register(UUID id, AbilityData abilityData) {
        this.abilityData.put(id, abilityData);
    }
    public AbilityData get(UUID id) {
        return this.abilityData.get(id);
    }
    public AbilityData get(String name) {
        for (UUID id : abilityData.keySet()) {
            AbilityData data = this.abilityData.get(id);
            if (data.getName().equals(name)) {
                return data;
            }
        }
        return null;
    }
    public List<AbilityData> getAll() {
        return new ArrayList<>(abilityData.values());
    }
    public List<String> getAllNames() {
        List<String> list = new ArrayList<>();
        for (UUID id : abilityData.keySet()) {
            list.add(abilityData.get(id).getName());
        }
        return list;
    }
    public boolean exists(UUID id) {
        return this.abilityData.containsKey(id);
    }
    public boolean exists(String name) {
        for (UUID id : abilityData.keySet()) {
            if (abilityData.get(id).getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    public void unregister(UUID id) {
        this.abilityData.remove(id);
    }
    public void unregister(String name) {
        for (UUID id : abilityData.keySet()) {
            if (abilityData.get(id).getName().equalsIgnoreCase(name)) {
                abilityData.remove(id);
                break;
            }
        }
    }
    public void unregister(AbilityData abilityData) {
        this.abilityData.remove(abilityData.getId());
    }


}
