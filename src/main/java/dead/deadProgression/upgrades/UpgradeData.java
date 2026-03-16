package dead.deadProgression.upgrades;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class UpgradeData {

    private final UUID id;
    private UUID abilityID;
    private String name;
    private List<Material> allowedItems;
    private Map<Integer, Double> valuesPerTier = new HashMap<>();
    private Map<Integer, List<ItemStack>> pricePerTier = new HashMap<>();

    public UpgradeData(UUID id, UUID abilityID, String name) {
        this.id = id;
        this.abilityID = abilityID;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }
    public UUID getAbilityID() {
        return abilityID;
    }
    public String getName() {
        return name;
    }
    public List<Material> getAllowedItems() {
        return allowedItems;
    }
    public Map<Integer, Double> getValuesPerTier() {
        return valuesPerTier;
    }
    public Map<Integer, List<ItemStack>> getPricePerTier() {
        return pricePerTier;
    }
    public void setAbilityID(UUID abilityID) {
        this.abilityID = abilityID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAllowedItems(List<Material> allowedItems) {
        this.allowedItems = allowedItems;
    }
    public void addAllowedItem(Material material) {
        this.allowedItems.add(material);
    }
    public void removeAllowedItem(Material material) {
        this.allowedItems.remove(material);
    }
    public void setValuesPerTier(Map<Integer, Double> valuesPerTier) {
        this.valuesPerTier = valuesPerTier;
    }
    public void addValuePerTier(int tier, double value) {
        valuesPerTier.put(tier, value);
    }
    public void setValueForTier(int tier, double value) {
        valuesPerTier.put(tier, value);
    }
    public void clearLastValue() {
        valuesPerTier.remove(valuesPerTier.size() - 1);
    }
    public void setPricePerTier(Map<Integer, List<ItemStack>> pricePerTier) {
        this.pricePerTier = pricePerTier;
    }
    public void addPricePerTier(int tier, List<ItemStack> items) {
        pricePerTier.put(tier, items);
    }
    public void setPriceForTier(int tier, List<ItemStack> items) {
        pricePerTier.put(tier, items);
    }
    public void clearLastPrice() {
        pricePerTier.remove(pricePerTier.size() - 1);
    }

}
