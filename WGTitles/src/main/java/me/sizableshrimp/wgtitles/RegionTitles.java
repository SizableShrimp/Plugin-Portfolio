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

import lombok.Value;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

class RegionTitles {
    @Value
    static class Title {
        String title;
        String subtitle;
    }

    private RegionTitles() {}

    private static Title getTitle(ConfigurationSection regionTitle) {
        if (regionTitle == null) return null;
        String title = ChatColor.translateAlternateColorCodes('&', regionTitle.getString("Title", ""));
        String subtitle = ChatColor.translateAlternateColorCodes('&', regionTitle.getString("Subtitle", ""));
        return new Title(title, subtitle);
    }

    static Title getEnterTitle(ConfigurationSection region) {
        ConfigurationSection enter = region.getConfigurationSection("Enter");
        return getTitle(enter);
    }

    static Title getLeaveTitle(ConfigurationSection region) {
        ConfigurationSection leave = region.getConfigurationSection("Leave");
        return getTitle(leave);
    }
}
