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
package me.sizableshrimp.votefly;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import static me.sizableshrimp.votefly.VoteFly.addFlyTime;
import static me.sizableshrimp.votefly.VoteFly.getTimeLeft;
import static me.sizableshrimp.votefly.VoteFly.setFlyTime;
import static me.sizableshrimp.votefly.VoteFly.fliers;
import static me.sizableshrimp.votefly.VoteFly.flyTime;
import static me.sizableshrimp.votefly.VoteFly.getFlyInfo;
import static me.sizableshrimp.votefly.VoteFly.getStringTime;
import static me.sizableshrimp.votefly.VoteFly.hasFlyTime;
import static me.sizableshrimp.votefly.VoteFly.saveFlyInfo;

public class EventListener implements Listener {
    @EventHandler
    public void onVoteEvent(VotifierEvent event) {
        Vote vote = event.getVote();
        Player player = Bukkit.getPlayer(vote.getUsername());
        if (player == null || !player.isOnline()) return;
        if (player.hasPermission("essentials.fly")) return;
        addFlyTime(player, flyTime.toMillis());
        player.sendMessage("§aYou now have " + getTimeLeft(player) + " of fly time! Thanks for voting!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("essentials.fly") || !hasFlyTime(player)) return;
        long time = fliers.get(player.getUniqueId()) - System.currentTimeMillis();
        saveFlyInfo(player.getUniqueId(), time);
        fliers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Long time = getFlyInfo(player.getUniqueId());
        if (time == null || player.hasPermission("essentials.fly")) return;
        setFlyTime(player, System.currentTimeMillis() + time);
        player.sendMessage("You have " + getStringTime(time) + " of fly time left because you logged off with some still active!");
    }

//    @EventHandler
//    public void onToggleFlight(PlayerToggleFlightEvent event) {
//        if (!event.getPlayer().getAllowFlight() && hasFlyTime(event.getPlayer()))
//            event.getPlayer().setAllowFlight(true);
//    }

    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent event) {
        if (hasFlyTime(event.getPlayer())) event.getPlayer().setAllowFlight(true);
    }
}
