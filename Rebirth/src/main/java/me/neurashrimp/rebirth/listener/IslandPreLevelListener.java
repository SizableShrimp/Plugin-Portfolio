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

import com.wasteofplastic.askyblock.events.IslandPreLevelEvent;
import me.neurashrimp.rebirth.main.Rebirth;
import me.neurashrimp.rebirth.main.RebirthManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class IslandPreLevelListener implements Listener {
    @EventHandler
    public void onIslandPreLevel(IslandPreLevelEvent event) {
        UUID owner = event.getIslandOwner();
        int rebirthLevel = RebirthManager.getRebirthLevel(owner);
        if (rebirthLevel == 0) return;

        long islandLevel = event.getLongLevel();
        double modifier = getModifier(rebirthLevel);
        event.setLongLevel(Math.round(islandLevel * modifier));
    }

    public static double getModifier(int rebirthLevel) {
        return (Rebirth.config.getDouble("IslandLevelModifier") * rebirthLevel) + 1;
    }
}
