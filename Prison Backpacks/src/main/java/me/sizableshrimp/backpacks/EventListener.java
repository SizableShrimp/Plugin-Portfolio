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

import me.clip.autosell.events.DropsToInventoryEvent;
import me.clip.autosell.events.SellAllEvent;
import me.clip.autosell.events.SignSellEvent;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class EventListener implements Listener {

    private FileConfiguration config = Main.instance.getConfig();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getType() != Material.CHEST) {
            return;
        }
        if (Main.holdingBackpack(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cYou cannot place backpacks!");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.CHEST) {
            if (!e.getInventory().getName().equals("Backpacks")) {
                return;
            }
            if (!(e.getWhoClicked() instanceof Player)) {
                return;
            }
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem().getType() == Material.CHEST && e.getCurrentItem().getItemMeta().getDisplayName().equals("§aBuy a backpack")) {
                Main.giveDefault(p);
                p.closeInventory();
                return;
            }
            if (e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE && e.getCurrentItem().getDurability() == (short) 1) {
                if (Main.holdingBackpack(p)) {
                    p.closeInventory();
                    boolean result = Main.checkBalance(p, (long) config.getInt("upgradeprice1"));
                    if (!result) {
                        p.sendMessage("§cYou do not have enough tokens to upgrade this backpack!");
                        return;
                    } else {
                        Main.upgradeBackpack(config.getInt("upgradeslot1"), p.getItemInHand());
                    }
                    Main.takeTokens(p, (long) config.getInt("upgradeprice1"));
                    p.sendMessage("§aYour backpack has been upgraded " + Integer.toString(config.getInt("upgradeslot1"
                    )) + " slots.");
                } else {
                    p.closeInventory();
                    p.sendMessage("§cHold a backpack in your hand to upgrade it!");
                }
            } else if (e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE && e.getCurrentItem().getDurability() == (short) 4) {
                if (Main.holdingBackpack(p)) {
                    boolean result = Main.checkBalance(p, (long) config.getInt("upgradeprice2"));
                    if (!result) {
                        p.sendMessage("§cYou do not have enough tokens to upgrade this backpack!");
                        return;
                    } else {
                        Main.upgradeBackpack(config.getInt("upgradeslot2"), p.getItemInHand());
                    }
                    Main.takeTokens(p, (long) config.getInt("upgradeprice2"));
                    p.sendMessage("§aYou backpack has been upgraded " + Integer.toString(config.getInt("upgradeslot2")) + " slots.");

                } else {
                    p.closeInventory();
                    p.sendMessage("§cHold a backpack in your hand to upgrade it!");
                }
            } else if (e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE && e.getCurrentItem().getDurability() == (short) 5) {
                if (Main.holdingBackpack(p)) {
                    boolean result = Main.checkBalance(p, (long) config.getInt("upgradeprice1"));
                    if (!result) {
                        p.sendMessage("§cYou do not have enough tokens to upgrade this backpack!");
                        return;
                    } else {
                        Main.upgradeBackpack(config.getInt("upgradeslot3"), p.getItemInHand());
                    }
                    Main.takeTokens(p, (long) config.getInt("upgradeprice3"));
                    p.sendMessage("§aYou backpack has been upgraded " + Integer.toString(config.getInt("upgradeslot3")) + " slots.");
                } else {
                    p.closeInventory();
                    p.sendMessage("§cHold a backpack in your hand to upgrade it!");
                }
            }
        } else if (e.getInventory().getType() == InventoryType.ANVIL && e.getSlotType() == InventoryType.SlotType.RESULT && Main.holdingBackpack((Player) e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropsToInv(DropsToInventoryEvent e) {
        if (e.getPlayer().getInventory().firstEmpty() == -1 && Main.isInventoryFull(e.getPlayer())) {
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
        }
        if (!e.getPlayer().getInventory().contains(Material.CHEST)) {
            return;
        }
        if (!config.getStringList("EnabledWorlds").contains(e.getPlayer().getWorld().getName())) {
            return;
        }
        if (!e.getPlayer().getItemInHand().getType().toString().contains("PICKAXE")) {
            return;
        }
        List<String> enabledItems = config.getStringList("EnabledItems")
                .stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        Set<String> dropNames = e.getDrops()
                .stream()
                .map(drop -> drop.getType().toString())
                .collect(Collectors.toSet());
        List<ItemStack> drops = new LinkedList<>(e.getDrops());
        if (!enabledItems.containsAll(dropNames)) {
            for (ItemStack drop : e.getDrops()) {
                if (!enabledItems.contains(drop.getType().toString())) {
                    drops.remove(drop);
                }
            }
        }
        List<ItemStack> packs = Main.searchForBP(e.getPlayer());
        if (packs.isEmpty()) {
            return;
        }
        for (ItemStack bpItem : packs) {
            int amount = 0;
            Backpack bp = Main.getBackpack(bpItem);
            if (bp == null || bp.getContents().isEmpty()) {
                continue;
            }
            List<ItemStack> contents = bp.getContents();
            for (ItemStack item : contents) {
                if (item != null) {
                    amount += item.getAmount();
                }
            }
            //            if (amount == (bp.getSlots() * 64)) {
            //                bpItem = fixStoredBlocks(bpItem, bp.getSlots());
            //                continue;
            //            }
            for (ItemStack item : drops) {
                contents.add(item);
                amount += item.getAmount();
            }
            while (amount > (bp.getSlots() * 64)) { //while the amount of items is greater than the backpack can hold
                if (contents.isEmpty()) {
                    break;
                }
                ItemStack chosenItem = contents.get((contents.size() - 1));
                chosenItem.setAmount(amount - chosenItem.getAmount());
                contents.remove(contents.size() - 1);
                amount -= chosenItem.getAmount();
                e.getPlayer().getInventory().addItem(chosenItem);
            }
            bp.setContents(contents);
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            ItemMeta bpMeta = bpItem.getItemMeta();
            List<String> bpLore = bpMeta.getLore();
            bpLore.set(1, "§fStored Items: §b" + amount);
            bpMeta.setLore(bpLore);
            bpItem.setItemMeta(bpMeta);
            return;
        }
    }

    @EventHandler
    public void onSignSell(SignSellEvent e) {
        execute(e.getShop().getPrices(), e.getPlayer(), (worth, amount) -> {
            e.setTotalCost(e.getTotalCost() + (worth * e.getMultiplier()));
            e.setTotalItems(e.getTotalItems() + amount);
        });
    }

    @EventHandler
    public void onSellAll(SellAllEvent e) {
        execute(e.getShop().getPrices(), e.getPlayer(), (worth, amount) -> {
            e.setTotalCost(e.getTotalCost() + (worth * e.getMultiplier()));
            e.setTotalItems(e.getTotalItems() + amount);
        });
    }

    private void execute(Map<ItemStack, Double> prices, Player player, BiConsumer<Double, Integer> consumer) {
        if (!config.getBoolean("SellBackpackItems")) {
            return;
        }
        List<ItemStack> list = Main.searchForBP(player);
        if (list.isEmpty()) {
            return;
        }
        for (ItemStack bpItem : list) {
            Integer id = Main.getID(bpItem);
            Backpack bp = Main.getBackpack(id);
            if (bp == null) {
                continue;
            }
            List<ItemStack> contents = bp.getContents();
            if (contents.isEmpty()) {
                continue;
            }
            Set<Material> materials = contents
                    .stream()
                    .map(ItemStack::getType)
                    .collect(Collectors.toSet());
            for (Map.Entry<ItemStack, Double> entry : prices.entrySet()) {
                if (!materials.contains(entry.getKey().getType())) {
                    continue;
                }
                ItemStack item = entry.getKey();
                double worth = 0d;
                int amount = 0;
                for (ItemStack contentsItem : new ArrayList<>(contents)) {
                    if (contentsItem.isSimilar(item)) {
                        amount += contentsItem.getAmount();
                        worth += entry.getValue() * contentsItem.getAmount();
                        contents.remove(contentsItem);
                    }
                }
                bp.setContents(contents);
                consumer.accept(worth, amount);

            }
            int stored = 0;
            for (ItemStack item : contents)
                stored += item.getAmount();
            ItemMeta bpMeta = bpItem.getItemMeta();
            List<String> bpLore = bpMeta.getLore();
            bpLore.set(1, "§fStored Items: §b" + stored);
            bpMeta.setLore(bpLore);
            bpItem.setItemMeta(bpMeta);
            return;
        }
    }
}
