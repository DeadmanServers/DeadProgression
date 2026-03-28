package dead.deadProgression.menu;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.progression.ProgressionRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import poa.poalib.items.CreateItem;
import poa.poalib.shaded.NBT;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    public abstract Inventory build();

    public abstract void handleClick(InventoryClickEvent event);

    public void open(Player player) {
        player.openInventory(build());
    }

    public static boolean isEmptyButton(ItemStack item) {
        return NBT.get(item, nbt -> {
            return nbt.hasTag("empty");
        });
    }
    public static boolean isBackButton(ItemStack item) {
        return NBT.get(item, nbt -> {
            return nbt.hasTag("back");
        });
    }
    public static boolean isNextButton(ItemStack item) {
        return NBT.get(item, nbt -> {
            return nbt.hasTag("next");
        });
    }

    public static List<String> buttonLore(List<String> addLore) {
        List<String> lore = new ArrayList<>();
        lore.add("");

        for (String s : addLore) {
            lore.add(s);
        }

        lore.add("");
        return lore;
    }

    public static final ProgressionRegistry progressionRegistry = DeadProgression.progressionRegistry;
    public static ItemStack next = CreateItem.createItem(Material.ARROW, "<green><bold>Next");
    public static ItemStack back = CreateItem.createItem(Material.ARROW, "<red><bold>Back");
    public static ItemStack close = CreateItem.createItem(Material.BARRIER, "<red><bold>Close");
    public static ItemStack placeholder = CreateItem.createItem(Material.STONE_BUTTON, "<grey><i:true>EMPTY", List.of(" ", " <dark_gray>• <green>Click to create new"));
    public static ItemStack glass = CreateItem.blackGlass();
    static {
        NBT.modify(placeholder, nbt -> {
           nbt.setString("empty", "empty");
        });
        NBT.modify(back, nbt -> {
            nbt.setString("back", "back");
        });
        NBT.modify(close, nbt -> {
            nbt.setString("close", "close");
        });
        NBT.modify(next, nbt -> {
            nbt.setString("next", "next");
        });
    }
    public static ItemStack brokenData =  CreateItem.createItem(Material.BARRIER, "<red><bold>BROKEN DATA");

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
