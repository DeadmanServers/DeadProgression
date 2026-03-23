package dead.deadProgression.menu;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.ability.AbilityData;
import dead.deadProgression.ability.AbilityRegistry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import poa.poalib.items.CreateItem;
import poa.poalib.shaded.NBT;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbilityMenu extends Menu {

    public Inventory inventory;
    private int page = 0;

    @Override
    public Inventory build() {
        this.inventory = Bukkit.createInventory(this, 27, MiniMessage.miniMessage().deserialize("&8Ability Menu"));

        AbilityRegistry registry = DeadProgression.abilityRegistry;
        List<AbilityData> all = registry.getAll();

        inventory.setItem(18, back);
        inventory.setItem(26, next);

        int index = (page * 44);

        for (int slot = 0; slot < 45; slot++) {
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

    }
}
