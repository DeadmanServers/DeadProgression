package dead.deadProgression.menu;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.ability.AbilityData;
import dead.deadProgression.ability.AbilityRegistry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import poa.poalib.items.CreateItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbilityEditorMenu extends Menu {

    private UUID abilityDataID;
    private int previousPage = 0;

    public int getPreviousPage() {
        return previousPage;
    }
    public void setPreviousPage(int previousPage) {
        this.previousPage = previousPage;
    }

    public UUID getAbilityDataID() {
        return abilityDataID;
    }
    public void setAbilityData(AbilityData abilityData) {
        this.abilityDataID = abilityData.getId();
    }
    public void setAbilityDataID(String abilityDataID) {
        try {
            this.abilityDataID = UUID.fromString(abilityDataID);
        } catch (IllegalArgumentException e) {
            DeadProgression.INSTANCE.getLogger().warning("Invalid ability data ID: " + abilityDataID);
        }
    }
    public void setAbilityDataID(UUID abilityDataID) {
        this.abilityDataID = abilityDataID;
    }

    private List<String> renameLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");

        AbilityData data = abilityRegistry.get(abilityDataID);
        if (data == null) {
            return lore;
        }

        String name = data.getName();
        if (name == null) {
            return lore;
        }
        lore.add(" <dark_gray><bold>- <gray>Current Name: <white>" + name);
        lore.add("");
        return lore;
    }

    @Override
    public Inventory build() {
        Inventory inv = Bukkit.createInventory(this, 27, MiniMessage.miniMessage().deserialize("<dark_gray>Ability Editor"));

        for (int i = 0; i <= 26; i++) {
            inv.setItem(i, empty);
        }

        inv.setItem(0, CreateItem.createItem(Material.OAK_SIGN, "<green><bold>Change Ability's Name", renameLore()));

        inv.setItem(18, back);

        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {

        if (event.getCurrentItem() == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        switch (event.getRawSlot()) {
            case 18:
                AbilityMenu abilityMenu = new AbilityMenu();
                abilityMenu.setPage(previousPage);
                abilityMenu.open(player);
        }


    }
}
