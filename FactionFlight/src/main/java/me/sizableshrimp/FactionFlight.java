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
