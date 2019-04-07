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
