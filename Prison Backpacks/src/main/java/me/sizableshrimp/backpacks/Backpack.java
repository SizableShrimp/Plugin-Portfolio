package me.sizableshrimp.backpacks;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Backpack {
	private List<ItemStack> items;
	private Integer slots;
	
	public Backpack(List<ItemStack> items, Integer slots) {
		this.items = items;
		this.slots = slots;
	}
	
	public List<ItemStack> getContents() {
		return items;
	}
	
	public Integer getSlots() {
		return slots;
	}
	
	public void setContents(List<ItemStack> newItems) {
		this.items = newItems;
	}
	
	public void setSlots(Integer newSlots) {
		this.slots = newSlots;
	}
}
