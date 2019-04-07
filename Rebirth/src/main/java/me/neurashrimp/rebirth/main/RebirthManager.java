package me.neurashrimp.rebirth.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.util.VaultHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.tehkode.permissions.PermissionEntity;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class RebirthManager {

    private static final String JSON_PATH = Rebirth.configFolder.getAbsolutePath() + "/players.json";
    private static final HashMap<UUID, Integer> REBIRTH_LEVELS = new HashMap<>();

    private RebirthManager() {}

    public static void rebirth(Player player) {
        ASkyBlock askyblock = ASkyBlock.getPlugin();

        askyblock.resetPlayer(player);
        askyblock.deletePlayerIsland(player.getUniqueId(), true);
        VaultHelper.econ.withdrawPlayer(player, VaultHelper.econ.getBalance(player));
        REBIRTH_LEVELS.put(player.getUniqueId(), getRebirthLevel(player.getUniqueId()) + 1);
        applyRebirthPerks(player);
        int rebirthLevel = getRebirthLevel(player.getUniqueId());
        List<UUID> teamMembers = ASkyBlockAPI.getInstance().getTeamMembers(player.getUniqueId());
        if (teamMembers.isEmpty()) {
            teamMembers.add(player.getUniqueId());
        }
        for (UUID uuid : teamMembers) {
            Player teamMember = Bukkit.getPlayer(uuid);
            if (teamMember != null) {
                applyRebirthEffects(player.getUniqueId(), teamMember);
            }
            applyRebirthPermissions(rebirthLevel, uuid);
        }
        applyRebirthEffects(player.getUniqueId(), player);
    }

    public static int getRebirthLevel(UUID uuid) {
        if (!REBIRTH_LEVELS.containsKey(uuid)) {
            return 0;
        }

        return REBIRTH_LEVELS.get(uuid);
    }

    public static boolean hasRebirthLevel(UUID uuid) {
        return REBIRTH_LEVELS.containsKey(uuid);
    }

    private static void applyRebirthPerks(Player player) {
        int rebirthLevel = getRebirthLevel(player.getUniqueId());
        ConfigurationSection rebirthPerks = getRebirthRewards(rebirthLevel);

        for (String command : rebirthPerks.getStringList("Commands")) {
            String modified = command.replace("%playername%", player.getPlayerListName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), modified);
        }

        String message = Rebirth.config.getString("RebirthMessage")
                .replace("%playername%", player.getPlayerListName())
                .replace("%rebirthlevel%", Integer.toString(rebirthLevel));
        message = ChatColor.translateAlternateColorCodes('&', message);

        player.sendMessage(message);
    }

    public static void applyRebirthPermissions(int rebirthLevel, UUID uuid) {
        rebirthPermissions(rebirthLevel, uuid, PermissionEntity::addPermission);
    }

    /**
     * Used to remove ALL permissions and tags from the user at the specified rebirth level and below. Meant for team
     * members who leave the island.
     *
     * @param rebirthLevel The specified rebirth level.
     * @param uuid The UUID of the player to remove the permissions from.
     */
    public static void removeRebirthPermissions(int rebirthLevel, UUID uuid) {
       BiConsumer<PermissionUser, String> consumer = PermissionEntity::removePermission;

        for (int i = 1; i <= rebirthLevel; i++) {
            rebirthPermissions(i, uuid, consumer);
        }
    }

    private static void rebirthPermissions(int rebirthLevel, UUID uuid, BiConsumer<PermissionUser, String> consumer) {
        PermissionUser permissionUser = PermissionsEx.getPermissionManager().getUser(uuid);
        ConfigurationSection rebirthRewards = getRebirthRewards(rebirthLevel);

        for (String permission : rebirthRewards.getStringList("Permissions")) {
            consumer.accept(permissionUser, permission);
        }
        rebirthRewards
                .getStringList("Tags")
                .forEach(tag -> consumer.accept(permissionUser, "deluxetags.tag." + tag.toLowerCase()));
    }

    public static void applyRebirthEffects(UUID islandOwner, Player player) {
        //get the effects for the player's rebirth level and below
        for (int i = 1; i <= getRebirthLevel(islandOwner); i++) {
            getRebirthRewards(i).getStringList("Effects").forEach(effect -> {
                int spaceIdx = effect.indexOf(' ');

                PotionEffectType potionType = PotionEffectType.getByName(effect.substring(0, spaceIdx));
                PotionEffect potionEffect = new PotionEffect(potionType, Integer.MAX_VALUE,
                        Integer.parseInt(effect.substring(spaceIdx)));
                player.addPotionEffect(potionEffect);
            });
        }
    }

    public static boolean canRebirth(UUID owner) {
        ASkyBlockAPI api = ASkyBlockAPI.getInstance();
        long islandLevel = api.getLongIslandLevel(owner);
        long nextRequiredLevel = getNextRebirthLevelRequirement(owner);

        if (nextRequiredLevel == -1) {
            return false;
        }
        return islandLevel >= nextRequiredLevel;
    }

    public static long getNextRebirthLevelRequirement(UUID owner) {
        int rebirthLevel = getRebirthLevel(owner);
        List<Long> rebirthEligibleLevels = Rebirth.config.getLongList("RebirthLevels");
        rebirthEligibleLevels.sort(Comparator.naturalOrder());
        int index = rebirthLevel + 1; //the rebirth level works as an index, but we need the next rebirth.

        if (index >= rebirthEligibleLevels.size()) {
            return -1;
        }

        return rebirthEligibleLevels.get(index);
    }

    public static ConfigurationSection getRebirthRewards(int rebirthLevel) {
        return Rebirth.config
                .getConfigurationSection("Rewards")
                .getConfigurationSection(Integer.toString(rebirthLevel));
    }

    public static void transferOwnership(UUID oldOwner, UUID newOwner) {
        if (!REBIRTH_LEVELS.containsKey(oldOwner)) {
            return;
        }

        REBIRTH_LEVELS.put(newOwner, REBIRTH_LEVELS.get(oldOwner));
        REBIRTH_LEVELS.remove(oldOwner);
    }

    public static void load() {
        try {
            Gson gson = new GsonBuilder().create();

            try (FileReader reader = new FileReader(JSON_PATH)) {
                Map<String, Integer> map = gson.fromJson(reader, new TypeToken<Map<String, Integer>>() {}.getType());
                map.forEach((str, lvl) -> REBIRTH_LEVELS.put(UUID.fromString(str), lvl));
            }
        } catch (FileNotFoundException ignored) {
            // ignore if the file was not found
        } catch (IOException e) {
            Rebirth.plugin.getLogger().log(Level.SEVERE, "Could not load rebirth statuses.", e);
        }
    }

    public static void save() {
        try {
            new File(JSON_PATH).createNewFile();
        } catch (IOException e) {
            Rebirth.plugin.getLogger().log(Level.SEVERE, "Could not create rebirth JSON file.", e);
            return;
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(REBIRTH_LEVELS);

        try (FileWriter writer = new FileWriter(JSON_PATH)) {
            writer.write(json);
        } catch (IOException e) {
            Rebirth.plugin.getLogger().log(Level.SEVERE, "Could not save rebirth statuses.", e);
        }
    }
}
