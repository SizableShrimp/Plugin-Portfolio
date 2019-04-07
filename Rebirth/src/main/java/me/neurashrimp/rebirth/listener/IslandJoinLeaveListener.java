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

import com.wasteofplastic.askyblock.events.IslandJoinEvent;
import com.wasteofplastic.askyblock.events.IslandLeaveEvent;
import me.neurashrimp.rebirth.main.RebirthManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IslandJoinLeaveListener implements Listener {

    @EventHandler
    public void onIslandLeave(IslandLeaveEvent event) {
        int rebirthLevel = RebirthManager.getRebirthLevel(event.getIslandOwner());
        RebirthManager.removeRebirthPermissions(rebirthLevel, event.getPlayer());
    }

    @EventHandler
    public void onIslandJoin(IslandJoinEvent event) {
        int rebirthLevel = RebirthManager.getRebirthLevel(event.getIslandOwner());

        for (int i = 1; i <= rebirthLevel; i++) {
            RebirthManager.applyRebirthPermissions(i, event.getPlayer());
        }
    }
}
