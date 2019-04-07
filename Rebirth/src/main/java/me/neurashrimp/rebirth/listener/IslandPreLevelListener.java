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
