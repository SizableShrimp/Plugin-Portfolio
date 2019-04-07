package me.neurashrimp.rebirth.placeholderapi;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neurashrimp.rebirth.main.Rebirth;
import me.neurashrimp.rebirth.main.RebirthManager;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class RebirthExpansion extends PlaceholderExpansion {

    private static final String FORMAT = Rebirth.config.getString("Format");

    @Override
    public String getIdentifier() {
        return "rebirth";
    }

    @Override
    public String getAuthor() {
        return "SizableShrimp and Neuranium";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equals("format")) {
            return execute(player.getUniqueId());
        }
        return null;
    }

    /**
     * Gets the rebirth chat format with the level replaced for the player.
     *
     * @param uuid A player UUID which can be a team member or the island owner.
     * @return The formatted rebirth chat string.
     */
    public static String execute(UUID uuid) {
        ASkyBlockAPI api = ASkyBlockAPI.getInstance();
        UUID islandOwner = uuid;
        if (!api.hasIsland(islandOwner) && !RebirthManager.hasRebirthLevel(islandOwner)) {
            if (!api.inTeam(islandOwner)) {
                return "";
            }
            islandOwner = api.getTeamLeader(uuid);
        }
        int rebirthLevel = RebirthManager.getRebirthLevel(islandOwner);
        if (rebirthLevel == 0) {
            return "";
        }
        String format = FORMAT
                .replace("%rebirthlevel%", Integer.toString(rebirthLevel));
        if (Rebirth.config.getBoolean("InsertFormatSpace")) {
            if (Rebirth.config.getBoolean("PrefixFormatSpace")) {
                format = " " + format;
            } else {
                format = format + " ";
            }
        }
        return ChatColor.translateAlternateColorCodes('&', format);
    }
}
