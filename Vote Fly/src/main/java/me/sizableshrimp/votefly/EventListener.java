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
