package dead.deadProgression.ability;

import dead.deadProgression.DeadProgression;

import java.util.List;
import java.util.UUID;

public class AbilityData {

    private static final AbilityRegistry abilityRegistry = DeadProgression.abilityRegistry;

    private final UUID id;
    private String name;
    private AbilityType type;
    private List<String> description;
    private double value;

    public AbilityData(UUID id, String name, AbilityType type, List<String> description, double value) {
        this.id = id;
        this.name = name;
        this.type = type;
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
    public AbilityType getType() {
        return type;
    }
    public boolean setName(String name) {
        if (!name.matches("^[a-zA-Z0-9&\\-,\\.\\s]+$")) {
            return false;
        }
        name = name.trim();
        this.name = name;
        abilityRegistry.save(id);
        return true;
    }
    public void setType(AbilityType type) {
        this.type = type;
        abilityRegistry.save(id);
    }
    public boolean setType(String type) {
        AbilityType abilityType = AbilityType.valueOf(type);
        if (abilityType == null) {
            return false;
        }
        setType(abilityType);
        abilityRegistry.save(id);
        return true;
    }
    public boolean setDescription(List<String> description) {
        for (String s : description) {
            if (!s.isBlank() && !s.matches("^[a-zA-Z0-9&\\-,\\.\\s]+$")) {
                return false;
            }
        }
        this.description = description;
        abilityRegistry.save(id);
        return true;
    }
    public void removeDescription(int descriptionLine) {
        if (!description.isEmpty()) {
            this.description.remove(descriptionLine);
            abilityRegistry.save(id);
        }
    }
    public void removeDescription(String description) {
        if (!description.isEmpty()) {
            this.description.remove(description);
            abilityRegistry.save(id);
        }
    }
    public void clearDescription() {
        this.description.clear();
        abilityRegistry.save(id);
    }
    public boolean addDescription(String description) {
        if (!description.isBlank() && !description.matches("^[a-zA-Z0-9&\\-,\\.\\s]+$")) {
            return false;
        }
        this.description.add(description);
        abilityRegistry.save(id);
        return true;
    }
    public void setDescriptionLine(int descriptionLine, String description) {
        this.description.set(descriptionLine, description);
        abilityRegistry.save(id);
    }
    public void setValue(double value) {
        this.value = value;
        abilityRegistry.save(id);
    }
}
