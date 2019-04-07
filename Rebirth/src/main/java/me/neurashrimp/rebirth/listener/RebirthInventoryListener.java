/*
 * Plugin-Portfolio - A portfolio of some of the Minecraft plugins made by SizableShrimp.
 *
 * Copyright (C) 2019 SizableShrimp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
