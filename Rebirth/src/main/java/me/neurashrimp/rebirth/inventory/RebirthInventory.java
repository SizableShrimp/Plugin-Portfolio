package me.neurashrimp.rebirth.inventory;

import me.neurashrimp.rebirth.listener.IslandPreLevelListener;
import me.neurashrimp.rebirth.main.RebirthManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RebirthInventory extends CraftInventoryCustom {

    private static final ItemStack CONFIRM_MODEL = new ItemStack(Material.DIAMOND_BLOCK);
    private static final ItemStack CANCEL_MODEL = new ItemStack(Material.BARRIER);
    private static final int CONFIRM_INDEX = 4;
    private static final int CANCEL_INDEX = 8;
    public static final String CONFIRM_NAME = "§aConfirm";
    public static final String CANCEL_NAME = "§cCancel";

    private final Player player;

    static {
        final ItemMeta confirmItemMeta = CONFIRM_MODEL.getItemMeta();
        final ItemMeta cancelItemMeta = CANCEL_MODEL.getItemMeta();

        List<String> confirmLore = new ArrayList<>();
        List<String> cancelLore = new ArrayList<>();

        confirmItemMeta.setDisplayName(CONFIRM_NAME);
        confirmLore.add("§6Rebirth %rebirthLevel%");
        confirmLore.add("§f§lPerks ");
        confirmLore.add("§7Permissions: ");
        confirmLore.add("§7Effects: ");
        confirmLore.add("§7Commands: ");
        confirmLore.add("§7Tags: ");
        confirmLore.add("§7Island level modifier: %islandLevelModifier%");

        cancelItemMeta.setDisplayName(CANCEL_NAME);
        cancelLore.add("§cExits the menu and cancels being reborn.");
        cancelLore.add("§7This menu will continue to be accessible via the /rebirth command.");

        confirmItemMeta.setLore(confirmLore);
        cancelItemMeta.setLore(cancelLore);

        CONFIRM_MODEL.setItemMeta(confirmItemMeta);
        CANCEL_MODEL.setItemMeta(cancelItemMeta);
    }

    private RebirthInventory(Player player) {
        super(null, 9, "Rebirth");

        this.player = player;

        this.setItem(CONFIRM_INDEX, CONFIRM_MODEL.clone());
        this.setItem(CANCEL_INDEX, CANCEL_MODEL.clone());
        this.configureItemLore();
    }

    private void configureItemLore() {
        //we want to display the NEXT rebirth level
        int rebirthLevel = RebirthManager.getRebirthLevel(this.player.getUniqueId()) + 1;
        ItemStack confirmItem = this.getItem(CONFIRM_INDEX);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        List<String> confirmLore = confirmMeta.getLore();
        ConfigurationSection rebirthRewards = RebirthManager.getRebirthRewards(rebirthLevel);
        List<String> rebirthPerms = rebirthRewards.getStringList("Permissions");
        List<String> rebirthFx = rebirthRewards.getStringList("Effects");
        List<String> rebirthCmds = rebirthRewards.getStringList("Commands");
        List<String> rebirthTags = rebirthRewards.getStringList("Tags");
        int fxIdx = 3 + rebirthPerms.size();
        int cmdIdx = 4 + rebirthPerms.size() + rebirthFx.size();
        int tgIdx = 5 + rebirthPerms.size() + rebirthFx.size() + rebirthCmds.size();

        confirmLore.set(0, confirmLore.get(0).replace("%rebirthLevel%", Integer.toString(rebirthLevel)));
        confirmLore.addAll(2 + 1, convertToLoreList(rebirthPerms));
        confirmLore.addAll(fxIdx + 1, convertToLoreList(rebirthFx));
        confirmLore.addAll(cmdIdx + 1, rebirthCmds
                .stream()
                .filter(str -> !str.isEmpty())
                .map(str -> "- " + str.replace("%playername%", this.player.getPlayerListName()))
                .collect(Collectors.toList()));
        confirmLore.addAll(tgIdx + 1, convertToLoreList(rebirthTags)
                .stream()
                .map(tag -> ChatColor.translateAlternateColorCodes('&', tag))
                .collect(Collectors.toList()));
        String islandLevelModifier = confirmLore.get(confirmLore.size() - 1).replace("%islandLevelModifier%",
                Double.toString(IslandPreLevelListener.getModifier(rebirthLevel)));
        confirmLore.set(confirmLore.size() - 1, islandLevelModifier);

        confirmMeta.setLore(confirmLore);
        confirmItem.setItemMeta(confirmMeta);
        this.setItem(CONFIRM_INDEX, confirmItem);
    }

    public static RebirthInventory getInventory(Player player) {
        return new RebirthInventory(player);
    }

    private static List<String> convertToLoreList(List<String> list) {
        return list.stream()
                .map(s -> "- " + s)
                .collect(Collectors.toList());
    }
}
