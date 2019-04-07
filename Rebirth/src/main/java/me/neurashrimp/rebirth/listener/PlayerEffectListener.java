package me.neurashrimp.rebirth.listener;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import me.neurashrimp.rebirth.main.RebirthManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

public class PlayerEffectListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        execute(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        execute(event.getPlayer());
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        execute(event.getPlayer());
    }

    private void execute(Player player) {
        UUID uuid = player.getUniqueId();
        ASkyBlockAPI api = ASkyBlockAPI.getInstance();
        if (!api.getIslandWorld().equals(player.getWorld())) return;
        boolean hasIsland = api.hasIsland(uuid);
        if (!hasIsland && !api.inTeam(uuid)) return;
        UUID owner = hasIsland ? uuid : api.getTeamLeader(uuid);

        //effects should be applied to team members
        if (RebirthManager.getRebirthLevel(owner) > 0) {
            RebirthManager.applyRebirthEffects(owner, player);
        }
    }
}
