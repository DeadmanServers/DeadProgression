package dead.deadProgression.categories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryData {

    private final UUID id;
    private String name;
    private List<UUID> upgrades = new ArrayList<>();

    public CategoryData(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<UUID> getUpgrades() {
        return upgrades;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUpgrades(List<UUID> upgrades) {
        this.upgrades = upgrades;
    }

    public void addUpgrade(UUID upgrade) {
        this.upgrades.add(upgrade);
    }

    public void removeUpgrade(UUID upgrade) {
        this.upgrades.remove(upgrade);
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


}
