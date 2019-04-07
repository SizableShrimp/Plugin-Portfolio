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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearChat extends JavaPlugin {
	private boolean chatlocked = false;
	private String prefix = "&8[&6&kii &3&lTekkian &5&lRealm &6&kii&r&8]";
	private String mainColor = "&3";
	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("clearchat") && sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("clearchat.clear")) {
				if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
					player.sendMessage("&6-------------------------------------------------------".replaceAll("&", "§"));
					player.sendMessage("&a/clearchat [-a|-s] &7or &a/cc [-a|-s] &7- &e\"-a\" makes the clear chat anonymous by excluding your username from the message. \"-s\" makes the clear chat silent with no message at all.".replaceAll("&", "§"));
					player.sendMessage("&a/chatlock [-a] &7or &a/cl [-a] &7- &e\"-a\" makes the chat lock anonymous by excluding your username from the message.".replaceAll("&", "§"));
					player.sendMessage("");
				}
				else if (args.length == 0) {
					player.performCommand("chc clear -s");
					Bukkit.broadcastMessage((prefix + " " + mainColor + player.getName() + " &7cleared the chat.").replaceAll("&", "§"));
					return true;
				}
				else if (args.length == 1 && args[0].equalsIgnoreCase("-a")) {
					player.performCommand("chc clear -s");
					Bukkit.broadcastMessage((prefix + " &7The chat was cleared.").replaceAll("&", "§"));
					return true;
				}
				else {
					player.sendMessage("§cInvalid usage! /clearchat [-a]");
					return true;
				}
			}
			else {
				player.sendMessage("§cInsufficient permission.");
				return true;
			}
		}
		if (cmd.getName().equalsIgnoreCase("chatlock") && sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("chatcontrol.commands.mute") && player.hasPermission("chatcontrol.commands.mute.silent")) {
				if (args.length == 0) {
					player.performCommand("chc mute -s");
					if (chatlocked == false) {
						Bukkit.broadcastMessage((prefix + " " + mainColor + player.getName() + " &7locked the chat.").replaceAll("&", "§"));
						chatlocked = true;
					}
					else {
						Bukkit.broadcastMessage((prefix + " " + mainColor + player.getName() + " &7unlocked the chat.").replaceAll("&", "§"));
						chatlocked = false;
					}
					return true;
				}
				else if (args.length == 1 && args[0].equalsIgnoreCase("-a")) {
					if (player.hasPermission("chatcontrol.commands.mute.anonymous")) {
						player.performCommand("chc mute -silent -anonymous");
						if (chatlocked == false) {
							Bukkit.broadcastMessage((prefix + " &7The chat was locked.").replaceAll("&", "§"));
							chatlocked = true;
						}
						else {
							Bukkit.broadcastMessage((prefix + " &7The chat was unlocked.").replaceAll("&", "§"));
							chatlocked = false;
						}
					}
					else {
						player.sendMessage("§cInsufficient permission.");
					}
					return true;
				}
				else if (args.length == 1 && args[0].equalsIgnoreCase("-s")) {
					player.performCommand("chc mute -silent");
					chatlocked = !chatlocked;
					return true;
				}
				else {
					player.sendMessage("§cInvalid usage! /chatlock [-a|-s]");
					return true;
				}
			}
			else {
				player.sendMessage("§cInsufficient permission.");
				return true;
			}
		}
		if (cmd.getName().equalsIgnoreCase("clearchat") && sender instanceof ConsoleCommandSender) {
			if (args.length == 0) {
				Bukkit.getServer().dispatchCommand(sender, "chc clear -s");
				Bukkit.broadcastMessage(("&8[&6FlameChat&8] &7Server cleared the chat.").replaceAll("&", "§"));
				return true;
			}
			else if (args.length == 1 && args[0].equalsIgnoreCase("-a")) {
				Bukkit.getServer().dispatchCommand(sender, "chc clear -s");
				Bukkit.broadcastMessage(("&8[&6FlameChat&8] &7The chat was cleared.").replaceAll("&", "§"));
				return true;
			}
			else if (args.length == 1 && args[0].equalsIgnoreCase("-s")) {
				Bukkit.getServer().dispatchCommand(sender, "chc clear -s");
				return true;
			}
			else {
				sender.sendMessage("§cInvalid usage! /clearchat [-a|-s]");
				return true;
			}
		}
		if (cmd.getName().equalsIgnoreCase("chatlock") && sender instanceof ConsoleCommandSender) {
			if (args.length == 0) {
				Bukkit.getServer().dispatchCommand(sender, "chc mute -s");
				if (chatlocked == false) {
					Bukkit.broadcastMessage(("&8[&6FlameChat&8] &7Server locked the chat.").replaceAll("&", "§"));
					chatlocked = true;
				}
				else {
					Bukkit.broadcastMessage(("&8[&6FlameChat&8] &7Server unlocked the chat.").replaceAll("&", "§"));
					chatlocked = false;
				}
				return true;
			}
			else if (args.length == 1 && args[0].equalsIgnoreCase("-a")) {
				Bukkit.getServer().dispatchCommand(sender, "chc mute -silent -anonymous");
				if (chatlocked == false) {
					Bukkit.broadcastMessage(("&8[&6FlameChat&8] &7The chat was locked.").replaceAll("&", "§"));
					chatlocked = true;
				}
				else {
					Bukkit.broadcastMessage(("&8[&6FlameChat&8] &7The chat was unlocked.").replaceAll("&", "§"));
					chatlocked = false;
				}
			}
			else if (args.length == 1 && args[0].equalsIgnoreCase("-s")) {
				Bukkit.getServer().dispatchCommand(sender, "chc mute -silent");
				chatlocked = !chatlocked;
				return true;
			}
			else {
				sender.sendMessage("§cInvalid usage! /chatlock [-a|-s]");
				return true;
			}
		}
		return false;
	}
}
