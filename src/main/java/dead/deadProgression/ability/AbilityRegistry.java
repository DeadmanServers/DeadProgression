package dead.deadProgression.ability;

import dead.deadProgression.DeadProgression;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbilityRegistry {

    public Map<UUID, AbilityData> abilityData = new HashMap<>();
    private final File file = new File(DeadProgression.INSTANCE.getDataFolder(), "data/abilitydata.yml");
    {
        file.mkdirs();
    }
    private final PoaYaml yml = PoaYaml.loadFromFile(file, true);


    public void load() {
        if (yml.isConfigurationSection("AbilityData")) {
            for (String data : yml.getConfigurationSection("AbilityData").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(data);
                    if (exists(id)){
                        continue;
                    }
                    String ymlVar = "AbilityData." + data;

                    AbilityType type = AbilityType.valueOf(ymlVar + ".Type");
                    String name = yml.getString(ymlVar + ".Name");
                    List<String> description = yml.getStringList(ymlVar + ".Description");
                    double value = yml.getDouble(ymlVar + ".Value");


                    register(id, new AbilityData(id, name, description, value));
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }
        }
    }

    public void register(UUID id, AbilityData abilityData) {
        this.abilityData.put(id, abilityData);
    }
    public AbilityData get(UUID id) {
        return this.abilityData.get(id);
    }
    public boolean exists(UUID id) {
        return this.abilityData.containsKey(id);
    }
    public void unregister(UUID id) {
        this.abilityData.remove(id);
    }



}
