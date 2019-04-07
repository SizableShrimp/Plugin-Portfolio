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
