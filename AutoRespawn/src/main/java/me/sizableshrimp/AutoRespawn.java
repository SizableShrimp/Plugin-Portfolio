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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.spawn.EssentialsSpawn;

public class AutoRespawn extends JavaPlugin implements Listener {
	private EssentialsSpawn es;
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();
		if ((p.getHealth() - e.getFinalDamage()) < 1) {
			e.setCancelled(true);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			p.getInventory().clear();
			clearArmor(p);
			p.teleport(es.getSpawn("default"));
		}
	}
	
	public void clearArmor(Player p) {
		PlayerInventory i = p.getInventory();
		i.setHelmet(new ItemStack(Material.AIR));
		i.setChestplate(new ItemStack(Material.AIR));
		i.setLeggings(new ItemStack(Material.AIR));
		i.setBoots(new ItemStack(Material.AIR));
	}
}
