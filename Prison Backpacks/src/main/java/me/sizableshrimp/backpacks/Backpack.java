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
