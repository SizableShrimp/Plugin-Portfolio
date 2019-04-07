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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import de.myzelyam.api.vanish.VanishAPI;

public class StaffOnline extends JavaPlugin implements Listener {

	public static StaffOnline plugin;
	public static String staffOnline;
	public static String helpers;
	public static String moderators;
	public static String administrators;
	public static String owners;
	public static boolean helpersonline;
	public static boolean moderatorsonline;
	public static boolean administratorsonline;
	public static boolean ownersonline;

	/*
	 * Made by SizableShrimp.
	 */
	
	@Override
	public void onEnable() {
		loadConfig();
	}

	@Override
	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("staff") && sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("staffonline.staff")) {	
				if (args.length == 0) {
					String staffonlineconfig =  plugin.getConfig().getString("StaffOnline").replaceAll("&", "§");
					String helpersconfig = plugin.getConfig().getString("Helpers").replaceAll("&", "§");
					String moderatorsconfig = plugin.getConfig().getString("Moderators").replaceAll("&", "§");
					String administratorsconfig = plugin.getConfig().getString("Administrators").replaceAll("&", "§");
					String ownersconfig = plugin.getConfig().getString("Owners").replaceAll("&", "§");
					StaffOnline.staffOnline = staffonlineconfig;
					StaffOnline.helpers = helpersconfig;
					StaffOnline.moderators = moderatorsconfig;
					StaffOnline.administrators = administratorsconfig;
					StaffOnline.owners = ownersconfig;
					StaffOnline.staffOnline = staffOnline.replace("%staffonline%", yesOrNoStaffOnline());
					StaffOnline.helpers = helpers.replace("%helpers%", getHelpers());
					StaffOnline.moderators = moderators.replace("%moderators%", getModerators());
					StaffOnline.administrators = administrators.replace("%administrators%", getAdministrators());
					StaffOnline.owners = owners.replace("%owners%", getOwners());
					sendStaffList(player);
					return true;
				}
				else if (args.length != 0 && !(player.hasPermission("staffonline.reload"))) {
					player.sendMessage("§cInvalid usage! /staff");
					return true;
				}
				else if (args.length != 0 && player.hasPermission("staffonline.reload")) {
					String arg1 = args[0];
					if (args.length == 1) {
						if (arg1.equalsIgnoreCase("reload")) {
							plugin.reloadConfig();
						}
						else if (!arg1.equalsIgnoreCase("reload")) {
							player.sendMessage("§cInvalid usage! /staff [reload]");
						}
					}
					else if (args.length != 1) {
						player.sendMessage("§cInvalid usage! /staff [reload]");
					}
				}
			}
			else if (!player.hasPermission("staffonline.staff")) {
				player.sendMessage("§cYou do not have permission to use this command!");
				return true;
			}

		}
		return false;
	}

	public static String getHelpers() {
		String helpers = "";
		String helpersnamecolor = plugin.getConfig().getString("HelpersNameColor").replaceAll("&", "§");
		int check = 0;
		Player[] arrayOfPlayers = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
		int j = arrayOfPlayers.length;
		for (int i = 0; i < j; i++) {
			Player player = arrayOfPlayers[i];
			if (player.hasPermission("staffonline.helper") && (!player.hasPermission("staffonline.moderator")) && (!player.hasPermission("staffonline.administrator")) && (!player.hasPermission("staffonline.owner"))) {
				helpers = helpers + helpersnamecolor + player.getName() + "§7, ";
				helpersonline = true;
			}
		}
		if (helpers.length() == 0) {
			helpers = "§7None";
			check = 1;
			helpersonline = false;
		}
		if (helpers.length() > 0 && check != 1) {
			helpers = helpers.substring(0, helpers.length() - 2);
		}
		return helpers;
	}

	public static String getModerators() {
		String moderators = "";
		String moderatorsnamecolor = plugin.getConfig().getString("ModeratorsNameColor").replaceAll("&", "§");
		int check = 0;
		Player[] arrayOfPlayers = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
		int j = arrayOfPlayers.length;
		int invisiblecheck = 0;
		for (int i = 0; i < j; i++) {
			Player player = arrayOfPlayers[i];
			invisiblecheck = 0;
			if (player.hasPermission("staffonline.moderator") && (!player.hasPermission("staffonline.administrator")) && (!player.hasPermission("staffonline.owner"))) {
				if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
					if (VanishAPI.isInvisible(player) == true) {
						invisiblecheck = 1;
					}
				}
				if (invisiblecheck != 1) {
					moderators = moderators + moderatorsnamecolor + player.getName() + "§7, ";
					moderatorsonline = true;
				}
			}
		}
		if (moderators.length() == 0 ) {
			moderators = "§7None";
			check = 1;
			moderatorsonline = false;
		}
		if (moderators.length() > 0 && check != 1) {
			moderators = moderators.substring(0, moderators.length() - 2);
		}
		return moderators;
	}

	public static String getAdministrators() {
		String administrators = "";
		String administratorsnamecolor = plugin.getConfig().getString("AdministratorsNameColor").replaceAll("&", "§");
		int check = 0;
		Player[] arrayOfPlayers = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
		int j = arrayOfPlayers.length;
		int invisiblecheck = 0;
		for (int i = 0; i < j; i++) {
			Player player = arrayOfPlayers[i];
			invisiblecheck = 0;
			if (player.hasPermission("staffonline.administrator") && (!player.hasPermission("staffonline.owner"))) {
				if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
					if (VanishAPI.isInvisible(player) == true) {
						invisiblecheck = 1;
					}
				}
				if (invisiblecheck != 1) {
					administrators = administrators + administratorsnamecolor + player.getName() + "§7, ";
					administratorsonline = true;
				}
			}
		}
		if (administrators.length() == 0) {
			administrators = "§7None";
			check = 1;
			administratorsonline = false;
		}
		if (administrators.length() > 0 && check != 1) {
			administrators = administrators.substring(0, administrators.length() - 2);
		}
		return administrators;
	}

	public static String getOwners() {
		String owners = "";
		String ownersnamecolor = plugin.getConfig().getString("OwnersNameColor").replaceAll("&", "§");
		int check = 0;
		Player[] arrayOfPlayers = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
		int j = arrayOfPlayers.length;
		int invisiblecheck = 0;
		for (int i = 0; i < j; i++) {
			Player player = arrayOfPlayers[i];
			invisiblecheck = 0;
			if (player.hasPermission("staffonline.owner")) {
				if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
					if (VanishAPI.isInvisible(player) == true) {
						invisiblecheck = 1;
					}
				}
				if (invisiblecheck != 1) {
					owners = owners + ownersnamecolor + player.getName() + "§7, ";
					ownersonline = true;
				}
			}
		}
		if (owners.length() == 0) {
			owners = "§7None";
			check = 1;
			ownersonline = false;
		}
		if (owners.length() > 0 && check != 1) {
			owners = owners.substring(0, owners.length() - 2);
		}
		return owners;
	}
	
	public static boolean staffOnline() {
		getHelpers();
		getModerators();
		getAdministrators();
		getOwners();
		if (helpersonline == true) {
			return true;
		}
		if (moderatorsonline == true) {
			return true;
		}
		if (administratorsonline == true) {
			return true;
		}
		if (ownersonline == true) {
			return true;
		}
		return false;
	}
	
	private String yesOrNoStaffOnline() {
		String staffOnline = "";
		boolean staffOnlineB = staffOnline();
		if (staffOnlineB == true) {
			staffOnline = "§aYes";
		}
		if (staffOnlineB == false) {
			staffOnline = "§cNo";
		}
		return staffOnline;
	}
	
	public static String getAllStaff() {
		String helpers = getHelpers();
		String moderators = getModerators();
		String administrators = getAdministrators();
		String owners = getOwners();
		helpers = helpers + "§7, ";
		moderators = moderators + "§7, ";
		administrators = administrators + "§7, ";
		owners = owners + "§7, ";
		if (helpers.equals("§7None§7, ")) {
			helpers = "";
		}
		if (moderators.equals("§7None§7, ")) {
			moderators = "";
		}
		if (administrators.equals("§7None§7, ")) {
			administrators = "";
		}
		if (owners.equals("§7None§7, ")) {
			owners = "";
		}
		String allStaff = helpers + moderators + administrators + owners;
		return allStaff;
	}
	
	private void loadConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                saveDefaultConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
	
	private void sendStaffList(Player p) {
		p.sendMessage(plugin.getConfig().getString("Header").replaceAll("&", "§"));
		p.sendMessage(StaffOnline.staffOnline);
		p.sendMessage(" ");
		p.sendMessage(StaffOnline.helpers);
		p.sendMessage(" ");
		p.sendMessage(StaffOnline.moderators);
		p.sendMessage(" ");
		p.sendMessage(StaffOnline.administrators);
		p.sendMessage(" ");
		p.sendMessage(StaffOnline.owners);
		p.sendMessage(plugin.getConfig().getString("Footer").replaceAll("&", "§"));
	}
} //Class ending bracket
