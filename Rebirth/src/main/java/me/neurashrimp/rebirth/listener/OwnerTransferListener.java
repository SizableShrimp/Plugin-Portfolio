package me.neurashrimp.rebirth.listener;

import com.wasteofplastic.askyblock.events.IslandChangeOwnerEvent;
import me.neurashrimp.rebirth.main.RebirthManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OwnerTransferListener implements Listener {

    @EventHandler
    public void onOwnerTransfer(IslandChangeOwnerEvent event) {
        RebirthManager.transferOwnership(event.getOldOwner(), event.getNewOwner());
    }
}
