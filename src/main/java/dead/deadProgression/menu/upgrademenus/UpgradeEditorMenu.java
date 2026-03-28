package dead.deadProgression.menu.upgrademenus;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.ability.AbilityData;
import dead.deadProgression.chatinputmanager.ChatInputManager;
import dead.deadProgression.chatinputmanager.PendingInput;
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
import java.util.function.Consumer;

public class UpgradeEditorMenu extends Menu {

    UUID upgradeDataID;
    int previousPage = 0;

    public void setUpgradeDataID(UUID upgradeDataID) {
        this.upgradeDataID = upgradeDataID;
    }
    public void setPreviousPage(int previousPage) {
        this.previousPage = previousPage;
    }
    public UUID getUpgradeDataID() {
        return upgradeDataID;
    }
    public int getPreviousPage() {
        return previousPage;
    }

    // Flow: Create Upgrade > Name Upgrade > Declare Ability for Upgrade > Declare items for upgrade > Declare max number of tiers (Possibly make it per item) > Declare value per tier > Declare price per tier

    // todo - Start with declaring global variables for the entire upgrade, but work on implementing per item overrides
    // todo   so we are able to declare separate values for different items within the same upgrade

    /*
    Buttons:
    Change name - Consumer
    Change max tiers - Consumer
    Edit allowed items - GUI
     - Add allowed Item - Sub GUI
     - Remove allowed Item - Click Event
    Edit values per tier - GUI
     - Add value - Consumer
     - Edit value - Consumer
    Edit prices per tier - GUI
     - Add Item per tier - GUI
     - Remove item per tier - Click Event
     - Edit item per tier - GUI
    Remove Upgrade - Consumer
     */

    @Override
    public Inventory build() {

        this.inventory = Bukkit.createInventory(this, 18, MiniMessage.miniMessage().deserialize("<dark_gary>Upgrade Editor"));

        UpgradeData upgradeData = UpgradeData.getUpgrade(upgradeDataID);
        if (upgradeData == null) {
            return inventory;
        }

        String name = upgradeData.getName();
        int maxTiers = upgradeData.getMaxTiers();
        String abilityName = "No Ability Set";

        UUID abilityID = upgradeData.getAbilityID();
        if (abilityID != null) {
            abilityName = AbilityData.get(abilityID).getName();
        }

        for (int i = 0 ; i <= 17; i++) {
            inventory.setItem(i, glass);
        }

        String e1 = " <dark_gray><b>- </b><gray>Current Name: <white>" + name;
        String e2 = " <dark_gray><b>- </b><gray>Current Ability: <white>" + abilityName;
        String e3 = " <dark_gray><b>- </b><gray>Current Max Tiers: <white>" + maxTiers;

        inventory.setItem(0, CreateItem.createItem(Material.OAK_SIGN, "<green><b>Change Upgrade's Name", buttonLore(List.of(e1))));
        inventory.setItem(1, CreateItem.createItem(Material.NETHER_STAR, "<green><b>Change Upgradable Ability", buttonLore(List.of(e2))));
        inventory.setItem(2, CreateItem.createItem(Material.ANVIL, "<green><b>Set Max Tiers", buttonLore(List.of(e3))));
        inventory.setItem(3, CreateItem.createItem(Material.CHEST, "<green>Edit Allowed Items"));
        inventory.setItem(4, CreateItem.createItem(Material.WRITABLE_BOOK, "<green>Edit Values Per Tier"));
        inventory.setItem(5, CreateItem.createItem(Material.EMERALD, "<green>Edit Prices Per Tier"));
        inventory.setItem(6, CreateItem.createItem(Material.BARRIER, "<red>DELETE UPGRADE"));

        inventory.setItem(9, back);

        return inventory;

    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        UpgradeData upgradeData = UpgradeData.getUpgrade(upgradeDataID);
        if (upgradeData == null) return;
        String name = upgradeData.getName();

        UUID id = player.getUniqueId();
        if (ChatInputManager.isAwaiting(id)) {
            ChatInputManager.cancel(id);
        }

        ItemStack clone = item.clone();
        if (isBackButton(clone)) {
            UpgradeMenu upgradeMenu = new UpgradeMenu();
            upgradeMenu.setPage(previousPage);
            upgradeMenu.open(player);
            return;
        }

        switch (event.getRawSlot()) {
            case 0 -> {
                Consumer<String> consumer = s -> {
                    upgradeData.setName(s);
                    Bukkit.getScheduler().runTask(DeadProgression.INSTANCE, () -> {
                        open(player);
                    });
                };
                PendingInput input = new PendingInput(consumer, "<red>You have cancelled setting a new name.");
                ChatInputManager.awaitInput(id, input);
                player.closeInventory();
                player.sendRichMessage("");
                player.sendRichMessage("<green>Setting name: <white>Type a new name for <yellow>" + name);
                return;
            }
            case 1 -> {
                UpgradeAbilitySelectorMenu upgradeAbilitySelectorMenu = new UpgradeAbilitySelectorMenu();
                upgradeAbilitySelectorMenu.setPreviousPageHolder(previousPage);
                upgradeAbilitySelectorMenu.setUpgradeDataID(upgradeDataID);
                upgradeAbilitySelectorMenu.open(player);
                return;
            }
            case 2 -> {
                Consumer<String> consumer = s -> {
                    try {
                        int maxTier = Integer.parseInt(s.trim());
                        if (maxTier <= 0) {
                            player.sendRichMessage("<red>ERROR: <white>That was not a valid tier amount.");
                            return;
                        }
                        upgradeData.setMaxTiers(maxTier);
                        player.sendRichMessage("<green>SUCCESS: <white>You have set the max tier for <yellow>" + name + " <white>to <yellow>" + maxTier);
                        Bukkit.getScheduler().runTask(DeadProgression.INSTANCE, () -> {
                            open(player);
                        });
                        return;
                    } catch (NumberFormatException e) {
                        player.sendRichMessage("<red>ERROR: <white>That was not a valid tier amount.");
                        return;
                    }
                };

                PendingInput input = new PendingInput(consumer, "<red>You have cancelled setting the max tier amount.");
                ChatInputManager.awaitInput(id, input);
                player.closeInventory();
                player.sendRichMessage("");
                player.sendRichMessage("<green>Changing Max Tiers: <white>Type the number of tiers for <yellow>" + name);
                return;
            }
            case 3 -> {
                UpgradeItemsMenu menu = new UpgradeItemsMenu();
                menu.setPreviousPageHolder(previousPage);
                menu.setUpgradeDataID(upgradeDataID);
                menu.open(player);
                return;
            }
        }

    }

}
