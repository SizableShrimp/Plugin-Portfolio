package me.neurashrimp.rebirth.listener;

import me.neurashrimp.rebirth.inventory.RebirthInventory;
import me.neurashrimp.rebirth.main.RebirthManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RebirthInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != null && event.getInventory().getTitle().equals("Rebirth")) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(RebirthInventory.CONFIRM_NAME)) {
                RebirthManager.rebirth((Player) event.getWhoClicked());
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(RebirthInventory.CANCEL_NAME)) {
                event.getView().close();
            }
        }
    }
}
