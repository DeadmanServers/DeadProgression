package dead.deadProgression.data;

import dead.deadProgression.DeadProgression;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.*;

public class AbilityData {

    public static final Map<UUID, AbilityData> abilityData = new HashMap<>();
    private static final File file = new File(DeadProgression.INSTANCE.getDataFolder(), "data/abilitydata.yml");
    static {
        file.getParentFile().mkdirs();
    }
    private static final PoaYaml yml = new PoaYaml().loadFromFile(file, true);

    public static void load() {
        if (yml.isConfigurationSection("AbilityData")) {
            for (String ability : yml.getConfigurationSection("AbilityData").getKeys(false)) {
                try {
                    UUID abilityID = UUID.fromString(ability);
                    if (abilityData.containsKey(abilityID)) {
                        continue;
                    }
                    AbilityData data = new AbilityData(abilityID);
                    abilityData.put(abilityID, data);
                } catch (Exception e) {
                    DeadProgression.INSTANCE.getLogger().warning("Failed to load ability data: " + ability);
                    continue;
                }
            }
        }
    }

    private final UUID abilityID;
    private String name = "New Ability";
    private AbilityType type = AbilityType.NO_VALUE;
    private List<String> description = new ArrayList<>();
    private double value = 0;
    private final String ymlVar;

    public AbilityData(UUID abilityID) {
        this.abilityID = abilityID;
        ymlVar = "AbilityData." + abilityID;
        //Creating a new Ability (Consider creating default values in config.yml)
        if (!yml.isSet(ymlVar)) {
            yml.set(ymlVar + ".Name", "New Ability");
            yml.set(ymlVar + ".Type", null);
            yml.set(ymlVar + ".Description", List.of());
            yml.set(ymlVar + ".Value", 0);

            yml.saveAsync(file);
        }

        // Getting created values
        if (yml.isSet(ymlVar + ".Name")) {
            this.name = yml.getString(ymlVar + ".Name");
        }
        if (yml.isSet(ymlVar + ".Type")) {
            this.type = AbilityType.valueOf(yml.getString(ymlVar + ".Type"));
        }
        if (yml.isSet(ymlVar + ".Description")) {
            this.description = yml.getStringList(ymlVar + ".Description");
        }
        if (yml.isSet(ymlVar + ".Value")) {
            this.value = yml.getDouble(ymlVar + ".Value");
        }

        abilityData.put(abilityID, this);
    }

    public UUID getAbilityID() {
        return abilityID;
    }
    public String getName() {
        return name;
    }
    public AbilityType getType() {
        return type;
    }
    public List<String> getDescription() {
        return description;
    }
    public double getValue() {
        return value;
    }
    public void setName(String name) {
        this.name = name;

        yml.set(ymlVar + ".Name", name);
        yml.saveAsync(file);
    }
    public void setType(AbilityType type) {
        this.type = type;

        yml.set(ymlVar + ".Type", type.toString());
        yml.saveAsync(file);
    }
    public void setDescription(List<String> description) {
        this.description = description;

        yml.set(ymlVar + ".Description", description);
        yml.saveAsync(file);
    }
    public void clearDescription() {
        this.description = null;

        yml.set(ymlVar + ".Description", List.of());
        yml.saveAsync(file);
    }
    public void removeDescriptionLine(int line) {
        description.remove(line);

        yml.set(ymlVar + ".Description", description);
        yml.saveAsync(file);
    }
    public void setDescriptionLine(int line, String newDescription) {
        if (line < description.size()) {
            description.set(line, newDescription);
        } else {
            description.add(line, newDescription);
        }

        yml.set(ymlVar + ".Description", newDescription);
        yml.saveAsync(file);
    }
    public void setValue(double value) {
        this.value = value;

        yml.set(ymlVar + ".Value", value);
        yml.saveAsync(file);
    }

    public static AbilityData get(UUID abilityID) {
        return abilityData.get(abilityID);
    }
    public static AbilityData get(String abilityID) {
        return abilityData.get(UUID.fromString(abilityID));
    }
    public static AbilityData getByName(String abilityName) {
        for (AbilityData data : abilityData.values()) {
            if (data.getName().equals(abilityName)) {
                return data;
            }
        }
        return null;
    }
    public static List<AbilityData> getAbilities() {
        return new ArrayList<>(abilityData.values());
    }
    public static List<String> getAbilitiesNames() {
        List<String> names = new ArrayList<>();
        for (AbilityData data : abilityData.values()) {
            names.add(data.getName());
        }
        return names;
    }
    public static void remove(UUID abilityID) {
        abilityData.remove(abilityID);

        yml.set("AbilityData." + abilityID, null);
        yml.saveAsync(file);
    }
    public void remove() {
        remove(abilityID);
    }

}
