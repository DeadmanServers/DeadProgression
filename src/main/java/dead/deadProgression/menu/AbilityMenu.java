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
import org.bukkit.inventory.ItemStack;
import poa.poalib.items.CreateItem;
import poa.poalib.shaded.NBT;

import java.util.List;
import java.util.UUID;

public class AbilityMenu extends Menu {

    private int page = 0;

    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public Inventory build() {
        this.inventory = Bukkit.createInventory(this, 27, MiniMessage.miniMessage().deserialize("<dark_gray>Ability Menu -- <gray>Page: " + page));

        AbilityRegistry registry = DeadProgression.abilityRegistry;
        List<AbilityData> all = registry.getAll();

        inventory.setItem(18, back);
        inventory.setItem(26, next);

        int index = (page * 18);

        for (int slot = 0; slot < 18; slot++) {
            if (all.size() <= index) {
                inventory.setItem(slot, placeholder);
                continue;
            }
            AbilityData data = all.get(index);
            if (data == null) {
                inventory.setItem(slot, brokenData);
                continue;
            }
            UUID id = data.getId();
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

        if (event.getCurrentItem() == null) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) return;

        switch (event.getRawSlot()) {
            case 18 -> {
                page--;
                open(player);
                return;
            }
            case 26 -> {
                page++;
                open(player);
                return;
            }
        }

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemStack clone = item.clone();
        String idString = NBT.get(clone, nbt ->{
            return nbt.getString("ID");
        });
        UUID id;
        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            player.sendRichMessage("<red><bold>ERROR: <yellow>Failed to parse ID.");
            return;
        }
        AbilityData data = abilityRegistry.get(id);
        if (data == null) {
            player.sendRichMessage("<red><bold>ERROR: <yellow>That ability does not exist.");
            return;
        }
        AbilityEditorMenu abilityEditorMenu = new AbilityEditorMenu();
        abilityEditorMenu.setAbilityData(data);
        abilityEditorMenu.setPreviousPage(page);
        abilityEditorMenu.open(player);
    }
}
