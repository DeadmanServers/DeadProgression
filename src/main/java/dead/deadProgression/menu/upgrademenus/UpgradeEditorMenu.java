package dead.deadProgression.menu.upgrademenus;

import dead.deadProgression.menu.Menu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

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

    @Override
    public Inventory build() {


        return null;

    }

    @Override
    public void handleClick(InventoryClickEvent event) {

    }

}
