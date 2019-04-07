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
package me.neurashrimp.rebirth.main;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import me.neurashrimp.rebirth.inventory.RebirthInventory;
import me.neurashrimp.rebirth.listener.*;
import me.neurashrimp.rebirth.placeholderapi.RebirthExpansion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

public class Rebirth extends JavaPlugin {

    public static final String PLUGIN_NAME = "Rebirth";

    public static File configFolder;
    public static FileConfiguration config;
    public static Rebirth plugin;

    @Override
    public void onEnable() {
        plugin = this;

        this.getLogger().info(PLUGIN_NAME + " enabled");
        this.getLogger().info("Loading assets...");
        this.createConfig();
        //registering into Placeholder API
        new RebirthExpansion().register();
        this.getLogger().info("Registering events...");
        this.registerEvents();

        RebirthManager.load();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Saving rebirth statuses...");
        RebirthManager.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandName, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (commandName.equalsIgnoreCase("rebirth")) {
            ASkyBlockAPI api = ASkyBlockAPI.getInstance();
            UUID uuid = player.getUniqueId();
            boolean hasIsland = api.hasIsland(uuid);
            if (!hasIsland && !api.inTeam(uuid)) {
                player.sendMessage("§cYou do not have an island!");
                return true;
            }
            if (!hasIsland && !api.getTeamLeader(uuid).equals(uuid)) {
                player.sendMessage("§cYou must be the owner of the island to execute this command!");
                return true;
            }

            int rebirthLevel = RebirthManager.getRebirthLevel(uuid);
            if (RebirthManager.getRebirthRewards(rebirthLevel + 1) == null) {
                player.sendMessage("§cYou are at the highest rebirth level.");
                return true;
            }
            if (!RebirthManager.canRebirth(uuid)) {
                long currentIslandLevel = api.getLongIslandLevel(uuid);
                long nextRequiredLevel = RebirthManager.getNextRebirthLevelRequirement(uuid);
                String message = String.format("§cYou cannot rebirth to level %d yet! You need to get to island level" +
                                " %d, which is %d levels away from your current island level. If you think this is a " +
                                "mistake, try doing §e/is level §cto recalculate your island level.",
                        rebirthLevel + 1,
                        nextRequiredLevel, nextRequiredLevel - currentIslandLevel);
                player.sendMessage(message);

                return true;
            }

            player.openInventory(RebirthInventory.getInventory(player));
        }

        return true;
    }

    private void createConfig() {
        this.saveDefaultConfig();

        configFolder = this.getDataFolder();
        config = this.getConfig();
    }

    private void registerEvents() {
        PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(new PlayerEffectListener(), this);
        manager.registerEvents(new IslandPreLevelListener(), this);
        manager.registerEvents(new RebirthInventoryListener(), this);
        manager.registerEvents(new OwnerTransferListener(), this);
        manager.registerEvents(new AsyncPlayerChatListener(), this);
        manager.registerEvents(new IslandJoinLeaveListener(), this);
    }
}
