package dead.deadProgression.menu;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.ability.AbilityRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import poa.poalib.items.CreateItem;

public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    public abstract Inventory build();

    public abstract void handleClick(InventoryClickEvent event);

    public void open(Player player) {
        player.openInventory(build());
    }

    public static final AbilityRegistry abilityRegistry = DeadProgression.abilityRegistry;
    public static ItemStack next = CreateItem.createItem(Material.ARROW, "<green><bold>Next");
    public static ItemStack back = CreateItem.createItem(Material.ARROW, "<red><bold>Back");
    public static ItemStack close = CreateItem.createItem(Material.BARRIER, "<red><bold>Close");
    public static ItemStack placeholder = CreateItem.createItem(Material.STONE_BUTTON, "<grey><i:true>empty");
    public static ItemStack empty = CreateItem.blackGlass();
    public static ItemStack brokenData =  CreateItem.createItem(Material.BARRIER, "<red><bold>BROKEN DATA");

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
