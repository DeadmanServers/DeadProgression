package dead.deadProgression.progression;

import java.util.List;
import java.util.UUID;

public class ProgressionData {

    private final UUID id;
    private String name;
    private List<UUID> categories;

    public ProgressionData(UUID id, String name, List<UUID> categories) {
        this.id = id;
        this.name = name;
        this.categories = categories;
    }
    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<UUID> getCategories() {
        return categories;
    }
    public void setCategories(List<UUID> categories) {
        this.categories = categories;
    }
    public void addCategory(UUID category) {
        this.categories.add(category);
    }
    public void removeCategory(UUID id) {
        categories.remove(id);
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
    public void setName(String name) {
        this.name = name;
    }
}
