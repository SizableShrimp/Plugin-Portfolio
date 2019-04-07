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
package me.sizableshrimp;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	public static HashMap<Player, Boolean> loggedIn = new HashMap<Player, Boolean>();

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {

	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setpin")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§4Error: §cYou are not a player. You cannot set a pin.");
				return true;
			}
			Player player = (Player) sender;
			if (profileCheck(player) && !player.hasPermission("securestaff.pin")) {
				player.sendMessage("§cYou already have a pin set. For security purposes, you cannot change your pin once set. Please ask an administrator to change it for you.");
			}
			if (args.length != 1) {
				player.sendMessage("§cInvalid usage! /setpin <pin>");
			}
			if (!realNumber(args[0])) {
				sender.sendMessage("§4Error: §cThe text entered is not a number.");
				return true;
			}
			Integer num = Integer.valueOf(args[0]);
			if (args[0].length() <= /*config max here*/4 && args[0].length() >= 4/*config min here*/) {
				try {
					File folder = new File(getDataFolder(), "profiles");
					if (!folder.exists()) folder.mkdirs();
					File f = new File(folder, player.getUniqueId().toString()+".yml");
					if (!f.exists()) f.createNewFile();
					YamlConfiguration yaml = new YamlConfiguration();
					yaml.load(f);
					yaml.set("loginPin", num);
					yaml.save(f);
				} catch (Exception e) {
					e.printStackTrace();
					return true;
				}
				player.sendMessage("§aPin successfully created.");
				return true;
			} else {
				if (args[0].length() >= 4/*config max here*/) {
					player.sendMessage("§4Error: §cThe pin entered is longer than the maximum pin length, "+Integer.toString(4/*config max here*/)+".");
				} else {
					player.sendMessage("§4Error: §cThe pin entered is shorter than the minimum pin length, "+Integer.toString(4/*config max here*/)+".");
				}
			}
		}
		else if (cmd.getName().equalsIgnoreCase("login")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§4Error: §cYou are not a player. You cannot log in.");
				return true;
			}
			Player player = (Player) sender;
			if (!player.hasPermission("securestaff.login")) {
				player.sendMessage("§cInsufficient permission.");
				return true;
			}
			if (loggedIn.get(player)) {
				player.sendMessage("§cYou are already logged in!");
				return true;
			}
			if (!profileCheck(player)) {
				player.sendMessage("§cYou do not have a pin, set it by using /setpin <pin>.");
				return true;
			}
			if (args.length != 1) {
				player.sendMessage("§cInvalid usage! /login <pin>");
				return true;
			}
			if (!realNumber(args[0])) {
				sender.sendMessage("§4Error: §cThe text entered is not a number.");
				return true;
			}
			Integer entered = Integer.parseInt(args[0]);
			try {
				File folder = new File(getDataFolder(), "profiles");
				if (!folder.exists()) {
					sender.sendMessage("§4Error: §cThe player specified does not have a pin.");
				}
				File f = new File(folder, player.getUniqueId().toString()+".yml");
				if (!f.exists()) f.createNewFile();
				YamlConfiguration yaml = new YamlConfiguration();
				yaml.load(f);
				Integer num = yaml.getInt("loginPin");
				if (num == entered) {
					loggedIn.put(player, true);
					player.sendMessage("§aWelcome back!");
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		}
		else if (cmd.getName().equalsIgnoreCase("pin")) {
			if (!sender.hasPermission("securestaff.pin")) {
				sender.sendMessage("§cInsufficient permission.");
				return true;
			}
			if (args.length != 2 && args.length != 3) {
				sender.sendMessage("§cInvalid usage! /pin <get|set> <player> [pin]");
				return true;
			}
			if (!args[0].equalsIgnoreCase("get") && args[0].equalsIgnoreCase("set")) {
				sender.sendMessage("§cInvalid usage! /pin <get|set> <player> [pin]");
				return true;
			}
			Player p = Bukkit.getPlayer(args[1]);
			if (p == null) {
				p = Bukkit.getOfflinePlayer(args[1]).getPlayer();
				if (Bukkit.getOfflinePlayer(args[1]) == null) {
					sender.sendMessage("§4Error: §cThe player specified has never logged into the server.");
					return true;
				}
				p = Bukkit.getOfflinePlayer(args[1]).getPlayer();
			}
			if (args[0].equalsIgnoreCase("get")) {
				try {
					File folder = new File(getDataFolder(), "profiles");
					if (!folder.exists()) {
						folder.mkdirs();
						sender.sendMessage("§4Error: §cThe player specified does not have a pin.");
					}
					File f = new File(folder, p.getUniqueId().toString()+".yml");
					if (!f.exists()) sender.sendMessage("§4Error: §cThe player specified does not have a pin.");
					YamlConfiguration yaml = new YamlConfiguration();
					yaml.load(f);
					Integer num = yaml.getInt("loginPin");
					sender.sendMessage(ChatColor.YELLOW+p.getName()+"'s pin is "+num.toString()+".");
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("set")) {
				if (!realNumber(args[2])) {
					sender.sendMessage("§4Error: §cThe text entered is not a number.");
					return true;
				}
				Integer num = Integer.valueOf(args[2]);
				if (args[2].length() <= /*config max here*/4 && args[2].length() >= 4/*config min here*/) {
					try {
						File folder = new File(getDataFolder(), "profiles");
						if (!folder.exists()) folder.mkdirs();
						File f = new File(folder, p.getUniqueId().toString()+".yml");
						if (!f.exists()) f.createNewFile();
						YamlConfiguration yaml = new YamlConfiguration();
						yaml.load(f);
						yaml.set("loginPin", num);
						yaml.save(f);
					} catch (Exception e) {
						e.printStackTrace();
						return true;
					}
					sender.sendMessage("§aA pin was successfully created for "+p.getName()+".");
					return true;
				} else {
					if (args[2].length() >= 1/*config max here*/) {
						sender.sendMessage("§4Error: §cThe pin entered is longer than the maximum pin length, "+Integer.toString(4/*config max here*/)+".");
					} else {
						sender.sendMessage("§4Error: §cThe pin entered is shorter than the minimum pin length, "+Integer.toString(4/*config max here*/)+".");
					}
				}
				return true;
			}
		}
		return false;
	}

	public Boolean profileCheck(Player p) {
		File[] flist = new File(getDataFolder(), "profiles").listFiles();
		if (flist != null && flist.length != 0) {
			String uuid = p.getUniqueId().toString();
			for (File f : flist) {
				String name = f.getName();
				if (name.contains(uuid)) {
					return true;
				}
			}
		}
		return false;
	}

	public Boolean realNumber(String s) {
		try {
			Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!e.getPlayer().hasPermission("securestaff.login")) return;
		if (!profileCheck(e.getPlayer())) {
			e.getPlayer().sendMessage("§cPlease set a pin using /setpin <pin> to secure your account. You will be asked this pin every time you log in.");
			return;
		}
		loggedIn.put(e.getPlayer(), false);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (loggedIn.containsKey(e.getPlayer())) loggedIn.remove(e.getPlayer());
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e) {
		if (loggedIn.get(e.getPlayer()) == false) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cPlease login with /login <pin>.");
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (loggedIn.getOrDefault(e.getPlayer(), false) == false) {
			if (!e.getMessage().toLowerCase().startsWith("/login")) {
				e.setCancelled(true);
				e.getPlayer().sendMessage("§cPlease login with /login <pin>.");
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		if (loggedIn.getOrDefault(e.getPlayer(), false) == false) {
			e.setCancelled(true);
			e.getPlayer().teleport(e.getFrom());
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onInteract(PlayerInteractEvent e) {
		if (loggedIn.getOrDefault(e.getPlayer(), false) == false) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryUse(InventoryInteractEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) return;
		if (loggedIn.getOrDefault(Bukkit.getPlayer(e.getWhoClicked().getName()), false) == false) {
			e.setCancelled(true);
			e.getWhoClicked().sendMessage("§cPlease login with /login <pin>.");
		}
	}
}
