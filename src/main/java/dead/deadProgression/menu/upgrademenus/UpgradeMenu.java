package dead.deadProgression.menu.upgrademenus;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.menu.Menu;
import dead.deadProgression.upgrades.UpgradeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import poa.poalib.items.CreateItem;
import poa.poalib.shaded.NBT;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class UpgradeMenu extends Menu {

    private int page = 0;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


    @Override
    public Inventory build() {

        this.inventory = Bukkit.createInventory(this, 54, MiniMessage.miniMessage().deserialize("<dark_gray>Upgrade Menu"));

        List<UpgradeData> all = UpgradeData.getAllUpgrades();

        inventory.setItem(45, back);
        inventory.setItem(53, next);

        ItemStack freeSpace = placeholder.clone();
        ItemMeta freeSpaceMeta = freeSpace.getItemMeta();
        List<Component> freeSpaceLore = new ArrayList<>();
        freeSpaceLore.add(Component.text(""));
        freeSpaceLore.add(MiniMessage.miniMessage().deserialize("  <dark_gray>• <green>Click to create new"));
        freeSpaceMeta.lore(freeSpaceLore);
        freeSpace.setItemMeta(freeSpaceMeta);

        int index = (page * 44);

        for (int slot = 0; slot < 45; slot++) {
            if (all.size() <= index) {
                inventory.setItem(slot, freeSpace);
                continue;
            }

            UpgradeData data = all.get(index);
            if (data == null) {
                inventory.setItem(slot, brokenData);
                continue;
            }

            UUID id = data.getId();
            if (id == null) {
                inventory.setItem(slot, brokenData);
                continue;
            }
            String name = data.getName();
            int allowedItemAmount = data.getAllowedItems().size();
            int tierAmount = data.getPricePerTier().size();

            ItemStack icon = CreateItem.createItem(Material.END_CRYSTAL, name);

            ItemMeta iconMeta = icon.getItemMeta();
            List<Component> iconLore = new ArrayList<>();
            iconLore.add(Component.text(""));
            iconLore.add(MiniMessage.miniMessage().deserialize("<gray>Allowed Items: <white>" + allowedItemAmount));
            iconLore.add(MiniMessage.miniMessage().deserialize("<gray>Tiers: <white>" + tierAmount));
            iconLore.add(Component.text(""));
            iconLore.add(MiniMessage.miniMessage().deserialize("  <dark_gray>• <green>Click to edit"));
            iconMeta.lore(iconLore);
            icon.setItemMeta(iconMeta);

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
        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        switch (event.getRawSlot()) {
            case 45 -> {
                page--;
                open(player);
                return;
            }
            case 53 -> {
                page++;
                open(player);
                return;
            }
        }

        if (isEmptyButton(item)) {
            UUID newID = UUID.randomUUID();
            try {
                UpgradeData upgradeData = new UpgradeData(newID);

                UpgradeEditorMenu upgradeEditorMenu = new UpgradeEditorMenu();
                upgradeEditorMenu.setUpgradeDataID(newID);
                upgradeEditorMenu.setPreviousPage(page);
                upgradeEditorMenu.open(player);
                return;
            } catch (Exception e) {
                DeadProgression.INSTANCE.getLogger().log(Level.SEVERE, "Error opening upgrade menu", e);
                player.sendRichMessage("<red>ERROR: <white>Failed to create a new upgrade.");
                return;
            }
        }

        ItemStack clone = item.clone();
        String idString = NBT.get(clone, nbt -> {
            return nbt.getString("ID");
        });
        UUID id;
        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            DeadProgression.INSTANCE.getLogger().log(Level.SEVERE, "Invalid ID: " + idString, e);
            player.sendRichMessage("<red><bold>ERROR: <yellow>Failed to parsed ID.");
            return;
        }
        UpgradeData upgradeData = UpgradeData.getUpgrade(id);
        if (upgradeData == null) {
            player.sendRichMessage("<red><bold>ERROR: <yellow>That upgrade doesn't exist.");
            return;
        }
        UpgradeEditorMenu upgradeEditorMenu = new UpgradeEditorMenu();
        upgradeEditorMenu.setUpgradeDataID(id);
        upgradeEditorMenu.setPreviousPage(page);
        upgradeEditorMenu.open(player);
        return;
    }
}
