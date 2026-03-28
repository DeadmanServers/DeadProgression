package dead.deadProgression.data;

import dead.deadProgression.DeadProgression;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.*;

public class CategoryData {

    public static final Map<UUID, CategoryData> categoryData = new HashMap<>();

    private static final File file = new File(DeadProgression.INSTANCE.getDataFolder(), "data/categorydata.yml");
    static {
        file.getParentFile().mkdirs();
    }

    private static final PoaYaml yml = new PoaYaml().loadFromFile(file, true);

    public static void load() {
        if (yml.isConfigurationSection("CategoryData")) {
            for (String category : yml.getConfigurationSection("CategoryData").getKeys(false)) {
                try {
                    UUID categoryID = UUID.fromString(category);
                    if (categoryData.containsKey(categoryID)) {
                        continue;
                    }
                    CategoryData data = new CategoryData(categoryID);
                    categoryData.put(categoryID, data);
                } catch (Exception e) {
                    DeadProgression.INSTANCE.getLogger().warning("Failed to load CategoryData: " + category);
                    continue;
                }
            }
        }
    }

    private final UUID categoryID;
    private String categoryName;
    private List<UUID> upgrades = new ArrayList<>();
    private final String ymlVar;

    public CategoryData(UUID categoryID) {
        this.categoryID = categoryID;
        ymlVar = "CategoryData." +  categoryID;
        // Creating a new Upgrade
        if (!yml.isSet(ymlVar)) {
            yml.set(ymlVar + ".Name", "New Category");
            yml.set(ymlVar + ".Upgrades", upgrades);

            yml.saveAsync(file);
        }

        // Getting created values
        if (yml.isSet(ymlVar + ".Name")) {
            categoryName = yml.getString(ymlVar + ".Name");
        }
        if (yml.isSet(ymlVar + ".Upgrades")) {
            for (String stringID : yml.getStringList(ymlVar + ".Upgrades")) {
                upgrades.add(UUID.fromString(stringID));
            }
        }

        categoryData.put(categoryID, this);
    }

    public UUID getCategoryID() {
        return categoryID;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public List<UUID> getUpgrades() {
        return upgrades;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;

        yml.set(ymlVar + ".Name", categoryName);
        yml.saveAsync(file);
    }
    public void setUpgrades(List<UUID> upgrades) {
        this.upgrades = upgrades;

        yml.set(ymlVar + ".Upgrades", upgrades);
        yml.saveAsync(file);
    }
    public void addUpgrade(UUID upgrade) {
        upgrades.add(upgrade);

        yml.set(ymlVar + ".Upgrades", upgrades);
        yml.saveAsync(file);
    }
    public void removeUpgrade(UUID upgrade) {
        upgrades.remove(upgrade);

        yml.set(ymlVar + ".Upgrades", upgrades);
        yml.saveAsync(file);
    }
    public boolean moveUpgrade(int newIndex, UUID upgrade) {

        int currentIndex = this.upgrades.indexOf(upgrade);

        if (newIndex < 0 || newIndex > upgrades.size()) {
            return false;
        }
        if (currentIndex == -1) {
            return false;
        }

        this.upgrades.remove(currentIndex);

        if (currentIndex <= newIndex) {
            this.upgrades.add(newIndex - 1, upgrade);
        } else {
            this.upgrades.add(newIndex, upgrade);
        }

        return true;
    }
    public void clearUpgrades() {
        upgrades.clear();

        yml.set(ymlVar + ".Upgrades", upgrades);
        yml.saveAsync(file);
    }

}
