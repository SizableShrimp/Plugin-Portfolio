package me.sizableshrimp.backpacks;

import me.realized.tokenmanager.api.TokenManager;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.sizableshrimp.backpacks.JsonItemStack.fromJsonArray;
import static me.sizableshrimp.backpacks.JsonItemStack.toJson;

public class Main extends JavaPlugin {
    static Main instance;
    private static final TokenManager tokenManager = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
    private static ItemStack noBP;
    private static Integer newBackpacks = 0;
    private static PacketPlayOutTitle title;
    private static PacketPlayOutTitle subtitle;
    private static PacketPlayOutTitle length;
    static HashMap<Integer, Backpack> backpacks = new HashMap<>();

    @Override
    public void onEnable() {
        createConfig();
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        instance = this;
        noBP = new ItemStack(Material.BARRIER, 1);
        ItemMeta noBPMeta = noBP.getItemMeta();
        noBPMeta.setDisplayName("§cYou do not have a backpack selected.");
        noBPMeta.setLore(Collections.singletonList("§7Hold a backpack in your hand to select it!"));
        noBP.setItemMeta(noBPMeta);
        IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + "Inventory Full" + "\",color:" + ChatColor.RED.name().toLowerCase() + "}");
        IChatBaseComponent chatSubtitle = ChatSerializer.a("{\"text\": \"" + "Sell Items At Mine" + "\",color:" + ChatColor.AQUA.name().toLowerCase() + "}");
        title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
        subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubtitle);
        length = new PacketPlayOutTitle(5, 60, 5);
    }

    @Override
    public void onDisable() {
        dataSave();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bp") && sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                player.sendMessage("§7--------------------------------------------------");
                player.sendMessage("§6/bp §f- §eThe main command to buy and upgrade backpacks.");
                player.sendMessage("§6/bp check §f- §eShows what items are in the backpack you are holding.");
                player.sendMessage("§6/bp clear §f- §eClears the backpack in your hand.");
                player.sendMessage("§6/bp help §f- §eGives the help menu you are seeing now.");
                player.sendMessage("§7--------------------------------------------------");
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                if (!holdingBackpack(player)) {
                    player.sendMessage("§cHold a backpack in your hand to clear it!");
                    return true;
                }
                Integer id = getID(player.getItemInHand());
                if (!backpacks.containsKey(id)) loadBackpack(id);
                Backpack bp = backpacks.get(id);
                if (bp == null) {
                    player.sendMessage("§cError: Cannot clear backpack because the backpack is invalid.");
                    return true;
                }
                bp.setContents(new ArrayList<>());
                ItemMeta meta = player.getItemInHand().getItemMeta();
                List<String> lore = meta.getLore();
                lore.set(1, "§fStored Items: §b0");
                meta.setLore(lore);
                player.getItemInHand().setItemMeta(meta);
                player.sendMessage("§cBackpack successfully cleared.");
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("check")) {
                if (!holdingBackpack(player)) {
                    player.sendMessage("§cHold a backpack in your hand to check its items!");
                    return true;
                }
                Integer id = getID(player.getItemInHand());
                if (!backpacks.containsKey(id)) loadBackpack(id);
                Backpack bp = backpacks.get(id);
                if (bp == null) {
                    player.sendMessage("§cError: Your backpack is invalid. Deleting invalid backpack...");
                    player.getInventory().remove(player.getItemInHand());
                    return true;
                }
                List<ItemStack> contents = bp.getContents();
                if (contents == null || contents.isEmpty()) {
                    player.sendMessage("§cError: Your backpack does not have any items.");
                    return true;
                }
                List<Material> materials = new ArrayList<>();
                for (ItemStack item : contents) {
                    if (!materials.contains(item.getType())) materials.add(item.getType());
                }
                String items = materials
                        .stream()
                        .map(Enum::toString)
                        .collect(Collectors.joining(", "));
                player.sendMessage("§bYour backpack contains: §f" + items);
                return true;
            } else player.openInventory(createGUI(player));
            return true;
        }
        return false;
    }

    static boolean isInventoryFull(Player p) {
        for (ItemStack item : p.getInventory().getContents()) {
            if ((item.getType() == Material.CHEST) || (item.getType() == Material.DIAMOND_AXE) || (item.getType() == Material.DIAMOND_PICKAXE) || (item.getType() == Material.DIAMOND_SWORD) || (item.getType() == Material.DIAMOND_SPADE))
                continue;
            if (item.getAmount() < item.getMaxStackSize()) {
                return false;
            }
        }
        List<ItemStack> list = searchForBP(p);
        if (list.isEmpty()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitle);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
            p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
            return true;
        }
        for (ItemStack bpItem : list) {
            Integer id = getID(bpItem);
            Backpack bp = getBackpack(id);
            if (bp == null) continue;
            int amount = 0;
            for (ItemStack item : bp.getContents()) {
                if (item != null) amount += item.getAmount();
            }
            if (amount != (bp.getSlots() * 64)) return false;
        }
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitle);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
        p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
        return true;
    }

    static void giveDefault(Player p) {
        if (tokenManager.getTokens(p).isPresent()) {
            p.sendMessage("§cYou do not have enough tokens to buy a backpack!");
            return;
        }
        long tokens = tokenManager.getTokens(p).getAsLong();
        long price = (long) instance.getConfig().getInt("backpackPrice");
        if (tokens < price) {
            p.sendMessage("§cYou do not have enough tokens to buy a backpack!");
            return;
        }
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage("§cYou do not have any inventory space to hold your backpack!");
            return;
        }
        tokenManager.setTokens(p, tokens - price);
        p.getInventory().addItem(createBackpack(9));
        p.sendMessage("§aSuccessfully bought a backpack!");

    }

    private static ItemStack createBackpack(Integer i) {
        List<ItemStack> contents = new ArrayList<>();
        ItemStack bp = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = bp.getItemMeta();
        meta.setDisplayName("§b§lBackpack");
        List<String> lore = new ArrayList<>();
        lore.add("§fCapacity: §b" + i.toString() + " slots (" + i * 64 + " items)");
        lore.add("§fStored Items: §b0");
        lore.add(" ");
        lore.add("§fUse §b/backpack §fto upgrade this backpack!");
        meta.setLore(lore);
        bp.setItemMeta(meta);
        try {
            int id = 0;
            File f = new File(instance.getDataFolder(), "data.yml");
            if (!f.exists()) {
                id = 1;
            } else {
                YamlConfiguration yaml = new YamlConfiguration();
                yaml.load(f);
                id = ((yaml.getKeys(false).size() + 1) + newBackpacks);
            }
            bp = setID(bp, id);
            Backpack backpack = new Backpack(contents, i);
            backpacks.put(id, backpack);
            newBackpacks++;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return bp;
    }

    static ItemStack upgradeBackpack(Integer amount, ItemStack item) {
        Backpack bp = backpacks.get(getID(item));
        bp.setSlots(bp.getSlots() + amount);
        return upgradeBackpackItem(bp.getSlots(), item);
    }

    private static ItemStack upgradeBackpackItem(int slots, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(0, "§fCapacity: §b" + slots + " slots (" + slots * 64 + " items)");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static List<ItemStack> searchForBP(Player p) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack item : p.getInventory()) {
            if (item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals("§b§lBackpack"))
                list.add(item);
        }
        return list;
    }

    static Integer getID(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (!nmsItem.hasTag()) return null;
        NBTTagCompound compound = nmsItem.getTag();
        return compound.getInt("BackpackID");
    }

    private static ItemStack setID(ItemStack item, Integer i) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        compound.set("BackpackID", new NBTTagInt(i));
        nmsItem.setTag(compound);
        item = CraftItemStack.asBukkitCopy(nmsItem);
        return item;
    }

    private static Inventory createGUI(Player p) {
        Inventory gui = Bukkit.createInventory(null, 27, "Backpacks");
        ItemStack orange = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
        ItemStack yellow = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
        ItemStack lime = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemStack backpack = new ItemStack(Material.CHEST, 1);
        ItemStack current;
        if (holdingBackpack(p)) {
            current = p.getItemInHand().clone();
            ItemMeta currentMeta = current.getItemMeta();
            currentMeta.setDisplayName("§a§lSelected Backpack");
            current.setItemMeta(currentMeta);
        } else {
            current = noBP;
        }
        String s = "s";
        if (instance.getConfig().getInt("upgradeslot1") == 1) s = "";
        ItemMeta orangeMeta = orange.getItemMeta();
        orangeMeta.setDisplayName("§7+" + instance.getConfig().getInt("upgradeslot1") + " slot" + s);
        orangeMeta.setLore(Collections.singletonList("§fCost: §a" + instance.getConfig().getInt("upgradeprice1") + " crystals"));
        orange.setItemMeta(orangeMeta);
        ItemMeta yellowMeta = yellow.getItemMeta();
        yellowMeta.setDisplayName("§7+" + instance.getConfig().getInt("upgradeslot2") + " slots");
        yellowMeta.setLore(Collections.singletonList("§fCost: §a" + instance.getConfig().getInt("upgradeprice2") + " crystals"));
        yellow.setItemMeta(yellowMeta);
        ItemMeta limeMeta = lime.getItemMeta();
        limeMeta.setDisplayName("§7+" + instance.getConfig().getInt("upgradeslot3") + " slots");
        limeMeta.setLore(Collections.singletonList("§fCost: §a" + instance.getConfig().getInt("upgradeprice3") + " crystals"));
        lime.setItemMeta(limeMeta);
        ItemMeta backpackMeta = backpack.getItemMeta();
        backpackMeta.setDisplayName("§aBuy a backpack");
        backpackMeta.setLore(Collections.singletonList("§7Cost: §a" + instance.getConfig().getInt("backpackPrice") + " crystals"));
        backpack.setItemMeta(backpackMeta);
        gui.setItem(4, current);
        gui.setItem(11, orange);
        gui.setItem(13, yellow);
        gui.setItem(15, lime);
        gui.setItem(22, backpack);
        return gui;
    }

    static boolean checkBalance(Player p, Long price) {
        if (!tokenManager.getTokens(p).isPresent()) return false;
        return tokenManager.getTokens(p).getAsLong() >= price;
    }

    static void takeTokens(Player p, Long price) {
        Long tokens = tokenManager.getTokens(p).isPresent() ? tokenManager.getTokens(p).getAsLong() : 0L;
        tokenManager.setTokens(p, tokens - price);
    }

    private void createConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) saveDefaultConfig();
    }

    private void dataSave() {
        try {
            File f = new File(getDataFolder(), "data.yml");
            if (!f.exists()) f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            for (Map.Entry<Integer, Backpack> entry : backpacks.entrySet()) {
                Backpack bp = entry.getValue();
                yaml.set(entry.getKey() + ".contents", toJson(bp.getContents()));
                yaml.set(entry.getKey() + ".slots", bp.getSlots());
            }
            yaml.save(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void dataSave(Player p) {
        try {
            File f = new File(getDataFolder(), "data.yml");
            if (!f.exists()) f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            List<ItemStack> packs = searchForBP(p);
            List<Integer> toRemove = new ArrayList<>(); //doing this because if an exception happens before the file is saved, it could delete backpacks prematurely.
            if (packs.isEmpty()) return;
            for (ItemStack bpItem : packs) {
                Integer id = getID(bpItem);
                if (id == null) continue;
                Backpack bp = getBackpack(id);
                yaml.set(id + ".contents", toJson(bp.getContents()));
                yaml.set(id + ".slots", bp.getSlots());
                toRemove.add(id);
            }
            yaml.save(f);
            for (Integer i : toRemove) {
                backpacks.remove(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadBackpack(Integer id) {
        if (id == null) return;
        try {
            File f = new File(instance.getDataFolder(), "data.yml");
            if (!f.exists()) return;
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            String json = yaml.getString(id.toString() + ".contents");
            if (json == null) return;
            Integer slots = yaml.getInt(id.toString() + ".slots");
            List<ItemStack> contents = fromJsonArray(json);
            backpacks.put(id, new Backpack(contents, slots));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Backpack getBackpack(ItemStack item) {
        Integer id = getID(item);
        return id == null ? null : getBackpack(id);
    }

    static Backpack getBackpack(Integer id) {
        if (!backpacks.containsKey(id)) loadBackpack(id);
        return backpacks.get(id);
    }

    static boolean holdingBackpack(Player p) {
        if (p.getItemInHand() == null) return false;
        return isBackpackItem(p.getItemInHand());
    }

    static boolean isBackpackItem(ItemStack item) {
        if (item.getType() != Material.CHEST) return false;
        return item.getItemMeta().getDisplayName().equals("§b§lBackpack");
    }

//    private List<ItemStack> itemStackListFromBase64(String data) {
//        List<ItemStack> items = new ArrayList<>();
//        try {
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
//            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
//            final int size = dataInput.readInt();
//            for (int i = 0; i < size; i++) {
//                items.add((ItemStack) dataInput.readObject());
//            }
//            dataInput.close();
//            return items;
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return items;
//    }
//
//    private String itemStackListToBase64(List<ItemStack> items) {
//        try {
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
//            dataOutput.writeInt(items.size());
//            for (ItemStack item : items) {
//                dataOutput.writeObject(item);
//            }
//            dataOutput.close();
//            return Base64Coder.encodeLines(outputStream.toByteArray());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    static ItemStack fixStoredBlocks(ItemStack item, Integer slots) {
//        String line = item.getItemMeta().getLore().get(1);
//        if (Integer.valueOf(line.substring(18)) == (slots * 64)) return item;
//        ItemMeta bpMeta = item.getItemMeta();
//        List<String> bpLore = bpMeta.getLore();
//        bpLore.set(1, "§fStored Items: §b" + (slots * 64));
//        bpMeta.setLore(bpLore);
//        item.setItemMeta(bpMeta);
//        return item;
//    }
}
