package dead.deadProgression.ability;

import java.util.List;
import java.util.UUID;

public class AbilityData {

    private final UUID id;
    private String name;
    private List<String> description;
    private double value;

    public AbilityData(UUID id, String name, List<String> description, double value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
    }
    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<String> getDescription() {
        return description;
    }
    public double getValue() {
        return value;
    }
    public boolean setName(String name) {
        if (!name.matches("^[a-zA-Z0-9\\s]+$")) {
            return false;
        }
        name = name.trim();
        this.name = name;
        return true;
    }
    public boolean setDescription(List<String> description) {
        for (String s : description) {
            if (!s.matches("^[a-zA-Z0-9\\s]+$")) {
                return false;
            }
        }
        this.description = description;
        return true;
    }
    public void removeDescription(Integer descriptionLine) {
        removeDescription(this.description.get(descriptionLine));
    }
    public void removeDescription(String description) {
        this.description.remove(description);
    }
    public void clearDescription() {
        this.description.clear();
    }
    public boolean addDescription(String description) {
        if (!description.matches("^[a-zA-Z0-9\\s]+$")) {
            return false;
        }
        this.description.add(description);
        return true;
    }
    public void setValue(double value) {
        this.value = value;
    }


}
