package dead.deadProgression.menu.abilitymenus;

import dead.deadProgression.ability.AbilityData;
import dead.deadProgression.ability.AbilityRegistry;
import dead.deadProgression.chatinputmanager.ChatInputManager;
import dead.deadProgression.chatinputmanager.PendingInput;
import dead.deadProgression.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import poa.poalib.items.CreateItem;
import poa.poalib.shaded.NBT;

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

        Inventory inv = Bukkit.createInventory(this, 27, "Ability Description Menu");

        AbilityData data = abilityRegistry.get(abilityDataID);
        if (data == null) {
            return inv;
        }

        List<String> description = data.getDescription();

        for (int i = 0; i < 27; i++) {

            inv.setItem(i, empty);


            if (i < 18) {
                if (description == null || description.isEmpty() || description.size() < i) {
                    inv.setItem(i, placeholder);
                    continue;
                }
                String s = description.get(i);
                if (s != null) {
                    ItemStack icon = CreateItem.createItem(Material.PAPER, s);

                    int nbtID = i;
                    NBT.modify(icon, nbt -> {
                        nbt.setInteger("descriptionID", nbtID);
                    });


                    inv.setItem(i, icon);
                }

            }

        }

        inv.setItem(18, back);

        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        AbilityData abilityData = abilityRegistry.get(abilityDataID);
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
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        ItemStack clone = currentItem.clone();

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

        AbilityData data = abilityRegistry.get(abilityDataID);
        if (data == null) {
            return;
        }

        if (descriptionID == -1) {
            if (click == ClickType.LEFT) {
                if (ChatInputManager.isAwaiting(id)) {
                    ChatInputManager.cancel(id);
                }

                Consumer<String> consumer = s -> {
                    data.addDescription(s);
                    open(player);
                };

                PendingInput input = new PendingInput(consumer, "<red>You have cancelled adding a new description.");
                ChatInputManager.awaitInput(player.getUniqueId(), input);
                player.closeInventory();
                player.sendRichMessage("");
                player.sendRichMessage("<green>Adding description: <white>Type out a line of text to add.");
                return;
            }
            if (click == ClickType.SHIFT_LEFT) {
                data.addDescription(" ");
                open(player);
            }
        }

        /* Existing Description ID

        LEFT_CLICK - Edit
        RIGHT_CLICK - Remove
        SHIFT_LEFT_CLICK - Set to Empty Line

         */


    }

}
