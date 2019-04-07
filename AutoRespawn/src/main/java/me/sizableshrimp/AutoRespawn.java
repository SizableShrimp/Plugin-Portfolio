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
