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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VoteFly extends JavaPlugin {
    private static VoteFly plugin;
    static Map<UUID, Long> fliers = new HashMap<>();
    static Duration flyTime;
    private static File dataFolder;

    @Override
    public void onEnable() {
        createConfig();

        int minutes = getConfig().getInt("FlyMinutes");
        if (minutes < 0) {
            minutes = getConfig().getDefaults().getInt("FlyMinutes");
        } else if (minutes == 0) {
            System.out.println("The fly minutes were found to be 0. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        flyTime = Duration.ofMinutes(minutes);
        plugin = this;
        dataFolder = getDataFolder();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Long time = getFlyInfo(player.getUniqueId());
            if (time == null || player.hasPermission("essentials.fly")) return;
            setFlyTime(player, System.currentTimeMillis() + time);
        }
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        for (Map.Entry<UUID, Long> entry : fliers.entrySet()) {
            if (hasFlyTime(entry.getKey())) saveFlyInfo(entry.getKey(), entry.getValue() - System.currentTimeMillis());
        }
    }

    static void saveFlyInfo(UUID uuid, long millis) {
        try {
            File f = new File(dataFolder, "fliers.yml");
            if (!f.exists()) f.createNewFile();
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
            yml.set(uuid.toString(), millis);
            yml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Long getFlyInfo(UUID uuid) {
        try {
            File f = new File(dataFolder, "fliers.yml");
            if (!f.exists()) return null;
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
            if (!yml.contains(uuid.toString())) return null;
            final long result = yml.getLong(uuid.toString());
            yml.set(uuid.toString(), null);
            yml.save(f);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("flytime") && sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
                help(player);
                return true;
            }
            if (player.hasPermission("votefly.add") && args.length > 3) {
                player.sendMessage("§cIncorrect usage! \n/flytime [player] \n/flytime add (player) (seconds)");
                return true;
            }
            if (player.hasPermission("votefly.add")) {
                if (args.length >= 1 && !args[0].equalsIgnoreCase("add")) {
                    player.sendMessage("§cIncorrect usage! /flytime add (player) (seconds)");
                    return true;
                }
                if (args.length == 3) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage("§c" + args[1] + " was not found online.");
                        return true;
                    }
                    if (target.hasPermission("essentials.fly")) {
                        player.sendMessage("§a" + target.getName() + " already has permission to fly without fly time.");
                        return true;
                    }
                    try {
                        Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cPlease enter a valid number.");
                        return true;
                    }
                    int num = Integer.parseInt(args[2]);
                    if (num < 0) {
                        player.sendMessage("§cPlease enter a number above zero.");
                        return true;
                    }
                    addFlyTime(player, num * 1000L);
                    player.sendMessage("§aGave " + target.getName() + " " + getTimeLeft(target) + " of fly time.");
                    target.sendMessage("§aYou have been given " + getTimeLeft(target) + " of fly time.");
                } else {
                    player.sendMessage("§cIncorrect usage! /flytime add (player) (seconds)");
                }
                return true;
            }
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage("§c" + args[0] + " was not found online.");
                    return true;
                }
                if (target.hasPermission("essentials.fly")) {
                    player.sendMessage("§a" + target.getName() + " already has permission to fly without voting.");
                    return true;
                }
                if (!hasFlyTime(target)) {
                    player.sendMessage("§c" + target.getName() + " does not have any fly time active.");
                    return true;
                }
                player.sendMessage("§a" + target.getName() + " has " + getTimeLeft(target) + " left of fly time from voting.");
                return true;
            }
            if (args.length != 0) {
                player.sendMessage("§cIncorrect usage! /flytime [player]");
                return true;
            }
            if (player.hasPermission("essentials.fly")) {
                player.sendMessage("§aYou already have permission to fly without voting.");
                return true;
            }
            if (!hasFlyTime(player)) {
                if (player.getAllowFlight()) {
                    final boolean flying = player.isFlying(); //makes sure allowFlight does not change this value
                    player.setAllowFlight(false);
                    if (flying) player.setFallDistance(-10000);
                }
                player.sendMessage("§cYou do not have any fly time active. You get " + getStringTime(flyTime.toMillis()) + " of fly time each time you vote.");
                return true;
            }
            if (!player.getAllowFlight()) player.setAllowFlight(true);
            player.sendMessage("§aYou have " + getTimeLeft(player) + " left of fly time from voting.");
            return true;
        }
        return false;
    }

    private void help(Player player) {
        if (player.hasPermission("votefly.add")) {
            player.sendMessage("§cIncorrect usage! \n/flytime [player] \n/flytime add (player) (seconds)");
        } else {
            player.sendMessage("§cIncorrect usage! /flytime [player]");
        }
    }

    private void createConfig() {
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) saveDefaultConfig();
    }

    private static void schedule(Player player) {
        if (!fliers.containsKey(player.getUniqueId()) || player.hasPermission("essentials.fly")) return;
        final long time = fliers.get(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || player.hasPermission("essentials.fly") || (int) Math.ceil(fliers.get(player.getUniqueId()) / 1000d) < (int) Math.ceil(System.currentTimeMillis() / 1000d))
                    return;
                removeFlyTime(player);
                player.sendMessage("§cYour vote fly time has expired!");
            }
        }.runTaskLater(plugin, millisToTicks(time - System.currentTimeMillis()));
    }

    private static void scheduleWarning(Player player) {
        if (!fliers.containsKey(player.getUniqueId()) || player.hasPermission("essentials.fly")) return;
        final long time = fliers.get(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || player.hasPermission("essentials.fly")) return;
                long currentTime = System.currentTimeMillis();
                long expireTime = fliers.get(player.getUniqueId());
                if (expireTime > currentTime && expireTime <= currentTime + TimeUnit.MINUTES.toMillis(1)) {
                    player.sendMessage("§cYour vote fly time will expire in the next minute. Please get to a safe place.");
                }
            }
        }.runTaskLater(plugin, millisToTicks(time - System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(59)));
    }

    static String getStringTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days);
        List<String> formats = new ArrayList<>();
        if (days > 0) formats.add(days == 1 ? days + " day" : days + " days");
        if (hours > 0) formats.add(hours == 1 ? hours + " hour" : hours + " hours");
        if (minutes > 0)
            formats.add(minutes == 1 ? minutes + " minute" : minutes + " minutes");
        if (seconds > 0)
            formats.add(seconds == 1 ? seconds + " second" : seconds + " seconds");
        if (formats.isEmpty()) return "less than a second";
        if (formats.size() == 2) return formats.get(0) + " and " + formats.get(1);
        if (formats.size() == 3) return formats.get(0) + ", " + formats.get(1) + ", and " + formats.get(2);
        if (formats.size() == 4)
            return formats.get(0) + ", " + formats.get(1) + ", " + formats.get(2) + ", and " + formats.get(3);
        return formats.get(0);
    }

    static String getTimeLeft(Player player) {
        return getStringTime(fliers.get(player.getUniqueId()) - System.currentTimeMillis());
    }

    static boolean hasFlyTime(Player player) {
        if (player.hasPermission("essentials.fly") || !fliers.containsKey(player.getUniqueId())) return false;
//        if (fliers.get(player.getUniqueId()) <= System.currentTimeMillis()) {
//            removeFlyTime(player);
//            return false;
//        }
//        return true;
        return fliers.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    static boolean hasFlyTime(UUID uuid) {
        return hasFlyTime(Bukkit.getPlayer(uuid));
    }

    static void removeFlyTime(Player player) {
        final boolean flying = player.isFlying(); //makes sure allowFlight does not change this value
        player.setAllowFlight(false);
        if (flying) player.setFallDistance(-10000);
        fliers.remove(player.getUniqueId());
    }

    /**
     * Sets the fly time for the player
     *
     * @param player     The player to receive fly time
     * @param expireTime The system time in milliseconds at which their fly time expires
     */
    static void setFlyTime(Player player, long expireTime) {
        player.setAllowFlight(true);
        fliers.put(player.getUniqueId(), expireTime);
        schedule(player);
        scheduleWarning(player);
    }

    /**
     * Adds the fly time to the player's current amount, or sets it if null
     *
     * @param player  The player to receive fly time
     * @param expires The extra time to add in milliseconds
     */
    static void addFlyTime(Player player, long expires) {
        long expireTime = fliers.getOrDefault(player.getUniqueId(), System.currentTimeMillis()) + expires;
        setFlyTime(player, expireTime);
    }

    private static long millisToTicks(long millis) {
        if (millis < 0) return 0L;
        return (long) (Math.ceil(millis / 1000d)) * 20;
    }
}
