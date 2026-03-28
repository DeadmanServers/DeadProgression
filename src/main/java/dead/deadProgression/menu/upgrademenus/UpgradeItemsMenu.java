package dead.deadProgression.menu.upgrademenus;

import dead.deadProgression.menu.Menu;
import dead.deadProgression.upgrades.UpgradeData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import poa.poalib.items.CreateItem;

import java.util.List;
import java.util.UUID;

public class UpgradeItemsMenu extends Menu {

    private UUID upgradeDataID;
    private int page = 0;
    private int previousPageHolder = 0;

    public UUID getUpgradeDataID() {
        return upgradeDataID;
    }
    public int getPage() {
        return page;
    }
    public int getPreviousPageHolder() {
        return previousPageHolder;
    }
    public void setUpgradeDataID(UUID upgradeDataID) {
        this.upgradeDataID = upgradeDataID;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public void setPreviousPageHolder(int previousPageHolder) {
        this.previousPageHolder = previousPageHolder;
    }

    @Override
    public Inventory build() {
        this.inventory = Bukkit.createInventory(this, 27, MiniMessage.miniMessage().deserialize("<dark_gray>Upgradable Items"));

        UpgradeData upgradeData = UpgradeData.getUpgrade(upgradeDataID);
        List<Material> allowedItems = upgradeData.getAllowedItems();

        inventory.setItem(18, back);
        inventory.setItem(26, next);

        int index = (page * 18);

        for (int slot = 0; slot < 18; slot++) {
            if (allowedItems.size() <= index) {
                inventory.setItem(slot, placeholder);
                continue;
            }
            Material material = allowedItems.get(index);
            if (material == null || material.isAir()) {
                inventory.setItem(slot, brokenData);
                continue;
            }
            ItemStack item = CreateItem.createItem(material, material.getItemTranslationKey());
            inventory.setItem(slot, item);
        }
        inventory.setItem(18, back);

        return inventory;
    }


    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.isEmpty() || item.getType() == Material.AIR) return;

        if (isEmptyButton(item)) {
            player.sendRichMessage(event.getClick().toString());
            return;
        }

    }
}
