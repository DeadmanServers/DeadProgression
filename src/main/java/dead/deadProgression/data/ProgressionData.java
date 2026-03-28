package dead.deadProgression.data;

import dead.deadProgression.DeadProgression;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.*;

public class ProgressionData {

    public static final Map<UUID, ProgressionData> progressionData = new HashMap<>();

    private static final File file = new File(DeadProgression.INSTANCE.getDataFolder(), "data/progressiondata.yml");
    static {
        file.getParentFile().mkdirs();
    }
    private static final PoaYaml yml = new PoaYaml().loadFromFile(file, true);

    public static void load() {
        if (yml.isConfigurationSection("ProgressionData")) {
            for (String progression : yml.getConfigurationSection("ProgressionData").getKeys(false)) {
                try {
                    UUID progressionID = UUID.fromString(progression);
                    if (progressionData.containsKey(progressionID)) {
                        continue;
                    }
                    ProgressionData data = new ProgressionData(progressionID);
                    progressionData.put(progressionID, data);
                } catch (Exception e) {
                    DeadProgression.INSTANCE.getLogger().warning("Failed to load ProgressionData: " + progression);
                    continue;
                }
            }
        }
    }


    private final UUID progressionID;
    private String name;
    private List<UUID> categories = new ArrayList<>();
    private final String ymlVar;

    public ProgressionData(UUID progressionID) {
        this.progressionID = progressionID;
        ymlVar = "ProgressionData." + progressionID;

        if (!yml.isSet(ymlVar)) {
            yml.set(ymlVar + ".Name", "New Progression");
            yml.set(ymlVar + ".Categories", List.of());

            yml.saveAsync(file);
        }


        if (yml.isSet(ymlVar + ".Name")) {
            this.name = yml.getString(ymlVar + ".Name");
        }
        if (yml.isSet(ymlVar + ".Categories")) {
            for (String categoryStringID : yml.getStringList(ymlVar + ".Categories")) {
                try {
                    UUID categoryID = UUID.fromString(categoryStringID);
                    if (categories.contains(categoryID)) {
                        continue;
                    }
                    categories.add(categoryID);
                } catch (Exception e) {
                    DeadProgression.INSTANCE.getLogger().warning("Failed to load ProgressionData: " + categoryStringID);
                    continue;
                }
            }
        }
        progressionData.put(progressionID, this);
    }

    public UUID getProgressionID() {
        return progressionID;
    }
    public String getName() {
        return name;
    }
    public List<UUID> getCategories() {
        return categories;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCategories(List<UUID> categories) {
        this.categories = categories;
    }
    public void addCategory(UUID categoryID) {
        categories.add(categoryID);
    }
    public void removeCategory(UUID categoryID) {
        categories.remove(categoryID);
    }
    public boolean moveCategory(int newIndex, UUID category) {
        int currentIndex = categories.indexOf(category);

        if (newIndex < 0 || newIndex > categories.size()) {
            return false;
        }
        if (currentIndex == -1) {
            return false;
        }

        this.categories.remove(currentIndex);

        if (currentIndex <= newIndex) {
            this.categories.add(newIndex - 1, category);
        } else {
            this.categories.add(newIndex, category);
        }
        return true;
    }
    public static ProgressionData getProgressionData(UUID progressionID) {
        return progressionData.get(progressionID);
    }
    public static ProgressionData getProgressionData(String name) {
        for (ProgressionData data : progressionData.values()) {
            if (data.getName().equals(name)) {
                return data;
            }
        }
        return null;
    }
    public static List<String> getProgressionNames() {
        List<String> list = new ArrayList<>();
        for (ProgressionData data : progressionData.values()) {
            list.add(data.getName());
        }
        return list;
    }
    public static void remove(UUID progressionID) {
        progressionData.remove(progressionID);

        yml.set("ProgressionData." + progressionID, null);
        yml.saveAsync(file);
    }
    public static void remove(ProgressionData data) {
        UUID dataID = data.getProgressionID();
        remove(dataID);
    }

}
