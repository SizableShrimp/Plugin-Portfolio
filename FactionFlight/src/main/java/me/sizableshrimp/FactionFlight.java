package me.sizableshrimp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

public class FactionFlight extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ffly") && sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("factionflight.fly")) {
                player.sendMessage("§cInsufficient permission.");
                return true;
            }
            MPlayer mp = MPlayer.get(player);
            if (!mp.hasFaction()) {
                player.sendMessage("§cYou are not in a faction!");
                return true;
            }
            if (mp.isInOwnTerritory()) {
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.sendMessage("§cFlight disabled.");
                    player.setFallDistance(-300);
                } else {
                    player.setAllowFlight(true);
                    player.sendMessage("§aFlight enabled.");
                }
            } else {
                player.sendMessage("§cYou are not in your faction's claim!");
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onChunkChange(PlayerMoveEvent e) {
        if (e.getFrom().getChunk().equals(e.getTo().getChunk())) return;
        Player p = e.getPlayer();
        if (!p.hasPermission("factionflight.fly")) return;
        if (p.isOp() || p.hasPermission("essentials.fly") || p.hasPermission("essentials.gamemode") || p.hasPermission("essentials.gamemode.creative")) return;
        MPlayer mp = MPlayer.get(p);
        if (!mp.hasFaction()) return;
        Location loc = e.getTo();
        Faction chunkfaction = BoardColl.get().getFactionAt(PS.valueOf(loc));
        if (mp.getFaction() == chunkfaction) return;
        if (p.getAllowFlight()) {
            p.setAllowFlight(false);
            p.sendMessage("§cYour flight was disabled because you left your faction claim.");
            if (p.isFlying()) {
                p.setFallDistance(-300);
            }
        }
    }
}
