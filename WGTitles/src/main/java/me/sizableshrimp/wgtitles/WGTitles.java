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
package me.sizableshrimp.wgtitles;

import com.destroystokyo.paper.Title;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public class WGTitles extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private static final int FADE_IN = 1;
    private int stay;
    private static final int FADE_OUT = 1;

    @EventHandler
    public void onRegionEnter(RegionEnteredEvent event) {
        execute(event.getPlayer(), event.getRegionName(), RegionTitles::getEnterTitle);
    }

    @EventHandler
    public void onRegionLeave(RegionLeftEvent event) {
        execute(event.getPlayer(), event.getRegionName(), RegionTitles::getLeaveTitle);
    }

    private void execute(Player player, String regionName, Function<ConfigurationSection, RegionTitles.Title> function) {
        if (player == null) return;
        ConfigurationSection region = config.getConfigurationSection(regionName);
        if (region == null) return;
        RegionTitles.Title title = function.apply(region);
        if (title == null) return;
        player.sendTitle(getTitle(title));
    }

    private Title getTitle(RegionTitles.Title regionTitle) {
        return new Title.Builder()
                .title(regionTitle.getTitle())
                .subtitle(regionTitle.getSubtitle())
                .fadeIn(FADE_IN)
                .stay(stay)
                .fadeOut(FADE_OUT)
                .build();
    }

    @Override
    public void onEnable() {
        createConfig();
        registerEvents();
    }

    @Override
    public void onDisable() {}

    private void createConfig() {
        this.saveDefaultConfig();

        config = this.getConfig();
        stay = config.getInt("TitleSeconds");
    }

    private void registerEvents() {
        PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(this, this);
    }
}
