package dead.deadProgression.menu;

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

    public static ItemStack next = CreateItem.createItem(Material.ARROW, "&a&lNext");
    public static ItemStack back = CreateItem.createItem(Material.ARROW, "&c&lBack");
    public static ItemStack close = CreateItem.createItem(Material.BARRIER, "&c&lClose");
    public static ItemStack placeholder = CreateItem.createItem(Material.PAPER, "&a&lPlaceholder");
    public static ItemStack empty = CreateItem.blackGlass();
    public static ItemStack brokenData =  CreateItem.createItem(Material.BARRIER, "&c&lBROKEN DATA");

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
