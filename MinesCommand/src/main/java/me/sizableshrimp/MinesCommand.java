package me.sizableshrimp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MinesCommand extends JavaPlugin implements Listener {
	String[] mines = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	List<Player> openguis = new ArrayList<Player>();

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mine") && sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				for (int i = 0; i < 26; i++) {
					String mine = mines[i];
					if (args[0].equalsIgnoreCase(mine)) {
						if (player.hasPermission("essentials.warps."+mine)) {
							player.performCommand("warp "+mine);
							return true;
						}
						player.sendMessage("§cYou cannot go to mine "+mine+"!");
					}
				}
				return true;
			}
			for (int i = 25; i > -1; i--) {
				String mine = mines[i];
				if (player.hasPermission("essentials.warps."+mine)) {
					player.performCommand("warp "+mine);
					return true;
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("mines") && sender instanceof Player) {
			Player player = (Player) sender;
			player.openInventory(getGUI(player));
			openguis.add(player);
			return true;
		}
		return false;
	}

	private Inventory getGUI(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, "Mines");
		for (int i = 0; i < 26; i++) {
			String mine = mines[i];
			if (p.hasPermission("essentials.warps."+mine)) {
				ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a§l"+mine);
				item.setItemMeta(meta);
				inv.setItem(i, item);
			} else {
				ItemStack item = new ItemStack(Material.REDSTONE_BLOCK, 1);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c§l"+mine);
				item.setItemMeta(meta);
				inv.setItem(i, item);
			}
		}

		return inv;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player) e.getWhoClicked();
		if (!(openguis.contains(p))) return;
		e.setCancelled(true);
		if (e.getCurrentItem().getType() == Material.EMERALD_BLOCK) {
			p.performCommand("warp "+e.getCurrentItem().getItemMeta().getDisplayName().charAt(4));
			p.closeInventory();
			return;
		} else if (e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
			p.sendMessage("§cYou cannot go to mine "+e.getCurrentItem().getItemMeta().getDisplayName().charAt(4)+"!");
			p.closeInventory();
			return;
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player)) return;
		Player p = (Player)e.getPlayer();
		if (openguis.contains(p)) openguis.remove(p);
	}
}
