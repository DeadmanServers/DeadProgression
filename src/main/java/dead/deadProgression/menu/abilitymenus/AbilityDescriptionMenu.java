package dead.deadProgression.menu.abilitymenus;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.data.AbilityData;
import dead.deadProgression.chatinputmanager.ChatInputManager;
import dead.deadProgression.chatinputmanager.PendingInput;
import dead.deadProgression.menu.Menu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import poa.poalib.items.CreateItem;
import poa.poalib.shaded.NBT;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class AbilityDescriptionMenu extends Menu {

    private UUID abilityDataID;
    private int previousPageHolder = 0;

    public UUID getAbilityDataID() {
        return abilityDataID;
    }

    public int getPreviousPageHolder() {
        return previousPageHolder;
    }

    public void setAbilityDataID(UUID abilityDataID) {
        this.abilityDataID = abilityDataID;
    }

    public void setPreviousPageHolder(int previousPageHolder) {
        this.previousPageHolder = previousPageHolder;
    }

    @Override
    public Inventory build() {

        this.inventory = Bukkit.createInventory(this, 27, MiniMessage.miniMessage().deserialize("<dark_gray>Ability Description Menu"));

        AbilityData data = AbilityData.get(abilityDataID);
        if (data == null) {
            return inventory;
        }

        List<String> description = data.getDescription();

        ItemStack freeSpace = placeholder.clone();
        ItemMeta meta = freeSpace.getItemMeta();
        List<Component> freeSpaceLore = new ArrayList<>();
        freeSpaceLore.add(Component.text(""));
        freeSpaceLore.add(MiniMessage.miniMessage().deserialize("<gray>Left-Click: <green>Create new"));
        freeSpaceLore.add(MiniMessage.miniMessage().deserialize("<gray>Middle-Click: <green>Add blank line"));
        meta.lore(freeSpaceLore);
        freeSpace.setItemMeta(meta);

        for (int i = 0; i < 27; i++) {

            inventory.setItem(i, glass);


            if (i < 18) {
                if (description == null || description.isEmpty() || description.size() <= i) {
                    inventory.setItem(i, freeSpace);
                    continue;
                }
                String s = description.get(i);
                if (s != null) {
                    ItemStack icon = CreateItem.createItem(Material.PAPER, s);

                    int nbtID = i;
                    NBT.modify(icon, nbt -> {
                        nbt.setInteger("descriptionID", nbtID);
                    });

                    ItemMeta iconMeta = icon.getItemMeta();
                    List<Component> iconLore = new ArrayList<>();
                    iconLore.add(Component.text(""));
                    iconLore.add(MiniMessage.miniMessage().deserialize("<gray>Left-Click: <green>Create new"));
                    iconLore.add(MiniMessage.miniMessage().deserialize("<gray>Middle-Click: <green>Add blank line"));
                    iconLore.add(MiniMessage.miniMessage().deserialize("<gray>Right-Click: <red>Remove line"));
                    iconMeta.lore(iconLore);
                    icon.setItemMeta(iconMeta);

                    inventory.setItem(i, icon);
                }

            }

        }

        inventory.setItem(18, back);

        return inventory;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }

        AbilityData abilityData = AbilityData.get(abilityDataID);
        if (abilityData == null) {
            return;
        }

        UUID id = player.getUniqueId();
        if (ChatInputManager.isAwaiting(id)) {
            ChatInputManager.cancel(id);
            return;
        }

        if (event.getRawSlot() == 18) {
            AbilityEditorMenu menu = new AbilityEditorMenu();
            menu.setAbilityDataID(abilityDataID);
            menu.setPreviousPage(previousPageHolder);
            menu.open(player);
            return;
        }

        ItemStack clone = item.clone();

        int descriptionID = NBT.get(clone, nbt -> {
            if (!nbt.hasTag("descriptionID")) {
                return -1;
            }
            return nbt.getInteger("descriptionID");
        });

        ClickType click = event.getClick();


        /* Empty Description ID

        LEFT_CLICK - Create New
        SHIFT_LEFT_CLICK - Insert Empty Line

         */

        AbilityData data = AbilityData.get(abilityDataID);
        if (data == null) {
            return;
        }

        if (ChatInputManager.isAwaiting(id)) {
            ChatInputManager.cancel(id);
        }

        if (descriptionID == -1) {
            int size = data.getDescription().size();
            if (click == ClickType.LEFT) {

                Consumer<String> consumer = s -> {
                    data.setDescriptionLine(size, s);
                    Bukkit.getScheduler().runTask(DeadProgression.INSTANCE, () -> {
                        open(player);
                    });
                };

                PendingInput input = new PendingInput(consumer, "<red>You have cancelled adding a new description.");
                ChatInputManager.awaitInput(id, input);
                player.closeInventory();
                player.sendRichMessage("");
                player.sendRichMessage("<green>Adding description: <white>Type out a line of text to add.");
                return;
            }
            if (click == ClickType.MIDDLE) {
                data.setDescriptionLine(size, " ");
                open(player);
            }
        }

        /* Existing Description ID

        LEFT_CLICK - Edit
        RIGHT_CLICK - Remove
        SHIFT_LEFT_CLICK - Set to Empty Line

         */
        if (descriptionID >= 0) {
            if (click == ClickType.LEFT) {

                Consumer<String> consumer = s -> {
                    List<String> description = abilityData.getDescription();
                    if (description == null || description.isEmpty() || description.size() <= descriptionID) {
                        return;
                    }
                    abilityData.setDescriptionLine(descriptionID, s);
                    Bukkit.getScheduler().runTask(DeadProgression.INSTANCE, t -> {
                        open(player);
                    });
                };

                PendingInput input = new PendingInput(consumer, "<red>You have cancelled setting a description line.");
                ChatInputManager.awaitInput(player.getUniqueId(), input);
                player.closeInventory();
                player.sendRichMessage("");
                player.sendRichMessage("<green>Changing Description: <white>Type out a line of text.");
                return;
            }
            if (click == ClickType.RIGHT) {
                abilityData.removeDescriptionLine(descriptionID);
                open(player);
            }
            if (click == ClickType.MIDDLE) {
                abilityData.setDescriptionLine(descriptionID, " ");
                open(player);
            }
        }

    }

}
