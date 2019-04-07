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
