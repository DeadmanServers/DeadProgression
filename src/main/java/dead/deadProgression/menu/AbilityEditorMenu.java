package dead.deadProgression.menu;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.ability.AbilityData;
import dead.deadProgression.ability.AbilityType;
import dead.deadProgression.chatinputmanager.ChatInputManager;
import dead.deadProgression.chatinputmanager.PendingInput;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import poa.poalib.items.CreateItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

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

    private List<String> buttonLore(List<String> addLore) {
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
        for (String s : addLore) {
            lore.add(s);
        }
        lore.add("");
        return lore;
    }

    @Override
    public Inventory build() {

        Inventory inv = Bukkit.createInventory(this, 27, MiniMessage.miniMessage().deserialize("<dark_gray>Ability Editor"));

        AbilityData abilityData = abilityRegistry.get(abilityDataID);
        if (abilityData == null) {
            return inv;
        }

        String name = abilityData.getName();

        List<String> description = new ArrayList<>();
        if (abilityData.getDescription() != null) {
            description = abilityData.getDescription();
        }

        AbilityType type = abilityData.getType();
        double value = abilityData.getValue();

        for (int i = 0; i <= 26; i++) {
            inv.setItem(i, empty);
        }

        String e1 = " <dark_gray><b>- </b><gray>Current Name: <white>" + name;
        String e2 = " <dark_gray><b>- </b><gray>Current Type: <white>" + type;
        String e3 = " <dark_gray><b>- </b><gray>Current Value: <white>" + value;

        inv.setItem(0, CreateItem.createItem(Material.OAK_SIGN, "<green><bold>Change Ability's Name", buttonLore(List.of(e1))));
        inv.setItem(1, CreateItem.createItem(Material.WRITABLE_BOOK, "<green><bold>Edit Ability's Lore", buttonLore(List.of(" <dark_gray><b>- </b><gray>Current Description: <white>" + " " + description))));
        inv.setItem(2, CreateItem.createItem(Material.BEACON, "<green><bold>Change Ability Type", buttonLore(List.of(e2))));
        inv.setItem(3, CreateItem.createItem(Material.EMERALD, "<green><bold>Change Ability's Value", buttonLore(List.of(e3))));

        inv.setItem(18, back);

        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {

        if (event.getCurrentItem() == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        AbilityData abilityData = abilityRegistry.get(abilityDataID);
        if (abilityData == null) return;


        /*

        0: Change Name
        1: Manage Description
        2: Change Type
        3: Change Value

         */
        switch (event.getRawSlot()) {
            case 0:
                UUID id = player.getUniqueId();
                if (ChatInputManager.isAwaiting(id)) {
                    ChatInputManager.cancel(id);
                }
                Consumer<String> consumer = s -> {
                    abilityData.setName(s);
                    player.sendRichMessage("<green><bold>Success!</b> <white>You have set the new name to: " + s);
                };

                PendingInput input = new PendingInput(consumer, "<red>You have cancelled setting the ability name.");
                ChatInputManager.awaitInput(id, input);
                player.closeInventory();
                player.sendRichMessage("");
                player.sendRichMessage("<green>Changing name: <white>Type a new name for the ability " + abilityData.getName());
                return;
            case 18:
                AbilityMenu abilityMenu = new AbilityMenu();
                abilityMenu.setPage(previousPage);
                abilityMenu.open(player);
        }


    }
}
