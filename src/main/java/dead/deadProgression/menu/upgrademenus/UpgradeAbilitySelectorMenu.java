package dead.deadProgression.menu.upgrademenus;

import dead.deadProgression.data.AbilityData;
import dead.deadProgression.menu.Menu;
import dead.deadProgression.data.UpgradeData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import poa.poalib.items.CreateItem;
import poa.poalib.shaded.NBT;

import java.util.List;
import java.util.UUID;

public class UpgradeAbilitySelectorMenu extends Menu {

    private UUID upgradeDataID;
    private int page = 0;
    private int previousPageHolder = 0;

    public int getPage() {
        return page;
    }

    public int getPreviousPageHolder() {
        return previousPageHolder;
    }

    public UUID getUpgradeDataID() {
        return upgradeDataID;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPreviousPageHolder(int previousPageHolder) {
        this.previousPageHolder = previousPageHolder;
    }

    public void setUpgradeDataID(UUID upgradeDataID) {
        this.upgradeDataID = upgradeDataID;
    }


    @Override
    public Inventory build() {
        this.inventory = Bukkit.createInventory(this, 54, MiniMessage.miniMessage().deserialize("<dark_gray>Ability Selector"));

        List<AbilityData> all = AbilityData.getAbilities();

        int index = (page * 45);

        inventory.setItem(45, back);
        if (index + 45 < all.size()) {
            inventory.setItem(53, next);
        }

        for (int slot = 0; slot < 45; slot++) {
            if (all.size() <= slot) {
                inventory.setItem(slot, placeholder);
                continue;
            }
            AbilityData data = all.get(index);
            if (data == null) {
                inventory.setItem(slot, brokenData);
            }
            UUID id = data.getAbilityID();
            String name = data.getName();
            List<String> description = data.getDescription();

            ItemStack icon = CreateItem.createItem(Material.NETHER_STAR, name, description);
            NBT.modify(icon, nbt -> {
                nbt.setString("ID", id.toString());
            });
            inventory.setItem(slot, icon);
            index++;
        }


        return inventory;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        ItemStack clone = item.clone();

        if (isBackButton(clone)) {
            page--;
            open(player);
            return;
        }
        if (isNextButton(clone)) {
            page++;
            open(player);
            return;
        }

        String idString = NBT.get(clone, nbt -> {
            return nbt.getString("ID");
        });
        if (idString == null || idString.isBlank()) return;
        UUID id;
        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            player.sendRichMessage("<red><b>ERROR:</b> <yellow>Failed to parse ID.");
            return;
        }

        UpgradeData upgradeData = UpgradeData.getUpgrade(upgradeDataID);
        upgradeData.setAbilityID(id);
        UpgradeEditorMenu upgradeEditorMenu = new UpgradeEditorMenu();
        upgradeEditorMenu.setPreviousPage(previousPageHolder);
        upgradeEditorMenu.setUpgradeDataID(upgradeDataID);
        upgradeEditorMenu.open(player);
    }
}
