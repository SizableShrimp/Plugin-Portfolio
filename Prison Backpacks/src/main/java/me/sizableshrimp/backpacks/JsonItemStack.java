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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Parse {@link ItemStack} to JSON
 *
 * @author DevSrSouza
 * @version 1.0
 * <p>
 * https://github.com/DevSrSouza/
 * You can find updates here https://gist.github.com/DevSrSouza
 */
public class JsonItemStack {

    private static final String[] BYPASS_CLASS = {"CraftMetaBlockState", "CraftMetaItem"
            /*Glowstone Support*/, "GlowMetaItem"};
    private static final Gson GSON = new Gson();

    private JsonItemStack() {
    }

    public static String toJson(List<ItemStack> items) {
        JsonArray array = new JsonArray();
        for (ItemStack item : items) {
            array.add(toJsonObject(item));
        }
        return GSON.toJson(array);
    }

    public static String toJson(ItemStack itemStack) {
        return GSON.toJson(toJsonObject(itemStack));
    }

    public static JsonObject toJsonObject(ItemStack itemStack) {
        JsonObject itemJson = new JsonObject();

        itemJson.addProperty("type", itemStack.getType().name());
        if (itemStack.getDurability() > 0) itemJson.addProperty("data", itemStack.getDurability());
        if (itemStack.getAmount() != 1) itemJson.addProperty("amount", itemStack.getAmount());

        if (itemStack.hasItemMeta()) {
            JsonObject metaJson = new JsonObject();

            ItemMeta meta = itemStack.getItemMeta();


            if (meta.hasDisplayName()) {
                metaJson.addProperty("displayname", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                JsonArray lore = new JsonArray();
                meta.getLore().forEach(str -> lore.add(new JsonPrimitive(str)));
                metaJson.add("lore", lore);
            }
            if (meta.hasEnchants()) {
                JsonArray enchants = new JsonArray();
                meta.getEnchants().forEach((enchantment, integer) -> enchants.add(new JsonPrimitive(enchantment.getName() + ":" + integer)));
                metaJson.add("enchants", enchants);
            }
            if (!meta.getItemFlags().isEmpty()) {
                JsonArray flags = new JsonArray();
                meta.getItemFlags().stream().map(ItemFlag::name).forEach(str -> flags.add(new JsonPrimitive(str)));
                metaJson.add("flags", flags);
            }

            for (String clazz : BYPASS_CLASS) {
                if (meta.getClass().getSimpleName().equals(clazz)) {
                    itemJson.add("item-meta", metaJson);
                    return itemJson;
                }
            }

            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (skullMeta.hasOwner()) {
                    JsonObject extraMeta = new JsonObject();
                    extraMeta.addProperty("owner", skullMeta.getOwner());
                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof BannerMeta) {
                BannerMeta bannerMeta = (BannerMeta) meta;
                JsonObject extraMeta = new JsonObject();
                extraMeta.addProperty("base-color", bannerMeta.getBaseColor().name());

                if (bannerMeta.numberOfPatterns() > 0) {
                    JsonArray patterns = new JsonArray();
                    bannerMeta.getPatterns()
                            .stream()
                            .map(pattern ->
                                    pattern.getColor().name() + ":" + pattern.getPattern().getIdentifier())
                            .forEach(str -> patterns.add(new JsonPrimitive(str)));
                    extraMeta.add("patterns", patterns);
                }

                metaJson.add("extra-meta", extraMeta);
            } else if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
                if (esmeta.hasStoredEnchants()) {
                    JsonObject extraMeta = new JsonObject();
                    JsonArray storedEnchants = new JsonArray();
                    esmeta.getStoredEnchants().forEach((enchantment, integer) -> storedEnchants.add(new JsonPrimitive(enchantment.getName() + ":" + integer)));
                    extraMeta.add("stored-enchants", storedEnchants);
                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                JsonObject extraMeta = new JsonObject();
                extraMeta.addProperty("color", Integer.toHexString(lameta.getColor().asRGB()));
                metaJson.add("extra-meta", extraMeta);
            } else if (meta instanceof BookMeta) {
                BookMeta bmeta = (BookMeta) meta;
                if (bmeta.hasAuthor() || bmeta.hasPages() || bmeta.hasTitle()) {
                    JsonObject extraMeta = new JsonObject();
                    if (bmeta.hasTitle()) {
                        extraMeta.addProperty("title", bmeta.getTitle());
                    }
                    if (bmeta.hasAuthor()) {
                        extraMeta.addProperty("author", bmeta.getAuthor());
                    }
                    if (bmeta.hasPages()) {
                        JsonArray pages = new JsonArray();
                        bmeta.getPages().forEach(str -> pages.add(new JsonPrimitive(str)));
                        extraMeta.add("pages", pages);
                    }
                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof PotionMeta) {
                PotionMeta pmeta = (PotionMeta) meta;
                if (pmeta.hasCustomEffects()) {
                    JsonObject extraMeta = new JsonObject();

                    JsonArray customEffects = new JsonArray();
                    pmeta.getCustomEffects().forEach(potionEffect -> customEffects.add(new JsonPrimitive(potionEffect.getType().getName()
                            + ":" + potionEffect.getAmplifier()
                            + ":" + potionEffect.getDuration() / 20)));
                    extraMeta.add("custom-effects", customEffects);

                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof FireworkEffectMeta) {
                FireworkEffectMeta femeta = (FireworkEffectMeta) meta;
                if (femeta.hasEffect()) {
                    FireworkEffect effect = femeta.getEffect();
                    JsonObject extraMeta = new JsonObject();

                    extraMeta.addProperty("type", effect.getType().name());
                    if (effect.hasFlicker()) extraMeta.addProperty("flicker", true);
                    if (effect.hasTrail()) extraMeta.addProperty("trail", true);

                    if (!effect.getColors().isEmpty()) {
                        JsonArray colors = new JsonArray();
                        effect.getColors().forEach(color ->
                                colors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                        extraMeta.add("colors", colors);
                    }

                    if (!effect.getFadeColors().isEmpty()) {
                        JsonArray fadeColors = new JsonArray();
                        effect.getFadeColors().forEach(color ->
                                fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                        extraMeta.add("fade-colors", fadeColors);
                    }

                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof FireworkMeta) {
                FireworkMeta fmeta = (FireworkMeta) meta;

                JsonObject extraMeta = new JsonObject();

                extraMeta.addProperty("power", fmeta.getPower());

                if (fmeta.hasEffects()) {
                    JsonArray effects = new JsonArray();
                    fmeta.getEffects().forEach(effect -> {
                        JsonObject jsonObject = new JsonObject();

                        jsonObject.addProperty("type", effect.getType().name());
                        if (effect.hasFlicker()) jsonObject.addProperty("flicker", true);
                        if (effect.hasTrail()) jsonObject.addProperty("trail", true);

                        if (!effect.getColors().isEmpty()) {
                            JsonArray colors = new JsonArray();
                            effect.getColors().forEach(color ->
                                    colors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                            jsonObject.add("colors", colors);
                        }

                        if (!effect.getFadeColors().isEmpty()) {
                            JsonArray fadeColors = new JsonArray();
                            effect.getFadeColors().forEach(color ->
                                    fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                            jsonObject.add("fade-colors", fadeColors);
                        }

                        effects.add(jsonObject);
                    });
                    extraMeta.add("effects", effects);
                }
                metaJson.add("extra-meta", extraMeta);
            } else if (meta instanceof MapMeta) {
                MapMeta mmeta = (MapMeta) meta;
                JsonObject extraMeta = new JsonObject();

                /* 1.11
                if(mmeta.hasLocationName()) {
                    extraMeta.addProperty("location-name", mmeta.getLocationName());
                }
                if(mmeta.hasColor()) {
                    extraMeta.addProperty("color", Integer.toHexString(mmeta.getColor().asRGB()));
                }*/
                extraMeta.addProperty("scaling", mmeta.isScaling());

                metaJson.add("extra-meta", extraMeta);
            }

            itemJson.add("item-meta", metaJson);
        }
        return itemJson;
    }

    public static List<ItemStack> fromJsonArray(String string) {
        List<ItemStack> items = new ArrayList<>();
        JsonElement element = new JsonParser().parse(string);
        if (!element.isJsonArray()) return items;
        JsonArray array = element.getAsJsonArray();
        for (JsonElement e : array) {
            if (!e.isJsonObject()) return items;
            items.add(fromJson(e.getAsJsonObject()));
        }
        return items;
    }

    public static ItemStack fromJson(JsonObject itemJson) {
        JsonElement typeElement = itemJson.get("type");
        JsonElement dataElement = itemJson.get("data");
        JsonElement amountElement = itemJson.get("amount");

        if (typeElement.isJsonPrimitive()) {

            String type = typeElement.getAsString();
            short data = dataElement != null ? dataElement.getAsShort() : 0;
            int amount = amountElement != null ? amountElement.getAsInt() : 1;

            ItemStack itemStack = new ItemStack(Material.getMaterial(type));
            itemStack.setDurability(data);
            itemStack.setAmount(amount);

            JsonElement itemMetaElement = itemJson.get("item-meta");
            if (itemMetaElement != null && itemMetaElement.isJsonObject()) {

                ItemMeta meta = itemStack.getItemMeta();
                JsonObject metaJson = itemMetaElement.getAsJsonObject();

                JsonElement displaynameElement = metaJson.get("displayname");
                JsonElement loreElement = metaJson.get("lore");
                JsonElement enchants = metaJson.get("enchants");
                JsonElement flagsElement = metaJson.get("flags");
                if (displaynameElement != null && displaynameElement.isJsonPrimitive()) {
                    meta.setDisplayName(displaynameElement.getAsString());
                }
                if (loreElement != null && loreElement.isJsonArray()) {
                    JsonArray jarray = loreElement.getAsJsonArray();
                    List<String> lore = new ArrayList<>(jarray.size());
                    jarray.forEach(jsonElement -> {
                        if (jsonElement.isJsonPrimitive()) lore.add(jsonElement.getAsString());
                    });
                    meta.setLore(lore);
                }
                if (enchants != null && enchants.isJsonArray()) {
                    JsonArray jarray = enchants.getAsJsonArray();
                    jarray.forEach(jsonElement -> {
                        if (jsonElement.isJsonPrimitive()) {
                            String enchantString = jsonElement.getAsString();
                            if (enchantString.contains(":")) {
                                try {
                                    String[] splitEnchant = enchantString.split(":");
                                    Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                                    int level = Integer.parseInt(splitEnchant[1]);
                                    if (enchantment != null && level > 0) {
                                        meta.addEnchant(enchantment, level, true);
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    });
                }
                if (flagsElement != null && flagsElement.isJsonArray()) {
                    JsonArray jarray = flagsElement.getAsJsonArray();
                    jarray.forEach(jsonElement -> {
                        if (jsonElement.isJsonPrimitive()) {
                            for (ItemFlag flag : ItemFlag.values()) {
                                if (flag.name().equalsIgnoreCase(jsonElement.getAsString())) {
                                    meta.addItemFlags(flag);
                                    break;
                                }
                            }
                        }
                    });
                }
                for (String clazz : BYPASS_CLASS) {
                    if (meta.getClass().getSimpleName().equals(clazz)) {
                        return itemStack;
                    }
                }

                JsonElement extrametaElement = metaJson.get("extra-meta");

                if (extrametaElement != null
                        && extrametaElement.isJsonObject()) {
                    try {
                        JsonObject extraJson = extrametaElement.getAsJsonObject();
                        if (meta instanceof SkullMeta) {
                            JsonElement ownerElement = extraJson.get("owner");
                            if (ownerElement != null && ownerElement.isJsonPrimitive()) {
                                SkullMeta smeta = (SkullMeta) meta;
                                smeta.setOwner(ownerElement.getAsString());
                            }
                        } else if (meta instanceof BannerMeta) {
                            JsonElement baseColorElement = extraJson.get("base-color");
                            JsonElement patternsElement = extraJson.get("patterns");
                            BannerMeta bmeta = (BannerMeta) meta;
                            if (baseColorElement != null && baseColorElement.isJsonPrimitive()) {
                                try {
                                    Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                                            .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(baseColorElement.getAsString()))
                                            .findFirst();
                                    color.ifPresent(bmeta::setBaseColor);
                                } catch (NumberFormatException ignored) {
                                }
                            }
                            if (patternsElement != null && patternsElement.isJsonArray()) {
                                JsonArray jarray = patternsElement.getAsJsonArray();
                                List<Pattern> patterns = new ArrayList<>(jarray.size());
                                jarray.forEach(jsonElement -> {
                                    String patternString = jsonElement.getAsString();
                                    if (patternString.contains(":")) {
                                        String[] splitPattern = patternString.split(":");
                                        Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                                                .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(splitPattern[0]))
                                                .findFirst();
                                        PatternType patternType = PatternType.getByIdentifier(splitPattern[1]);
                                        if (color.isPresent() && patternType != null) {
                                            patterns.add(new Pattern(color.get(), patternType));
                                        }
                                    }
                                });
                                if (!patterns.isEmpty()) bmeta.setPatterns(patterns);
                            }
                        } else if (meta instanceof EnchantmentStorageMeta) {
                            JsonElement storedEnchantsElement = extraJson.get("stored-enchants");
                            if (storedEnchantsElement != null && storedEnchantsElement.isJsonArray()) {
                                EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
                                JsonArray jarray = storedEnchantsElement.getAsJsonArray();
                                jarray.forEach(jsonElement -> {
                                    if (jsonElement.isJsonPrimitive()) {
                                        String enchantString = jsonElement.getAsString();
                                        if (enchantString.contains(":")) {
                                            try {
                                                String[] splitEnchant = enchantString.split(":");
                                                Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                                                int level = Integer.parseInt(splitEnchant[1]);
                                                if (enchantment != null && level > 0) {
                                                    esmeta.addStoredEnchant(enchantment, level, true);
                                                }
                                            } catch (NumberFormatException ignored) {
                                            }
                                        }
                                    }
                                });
                            }
                        } else if (meta instanceof LeatherArmorMeta) {
                            JsonElement colorElement = extraJson.get("color");
                            if (colorElement != null && colorElement.isJsonPrimitive()) {
                                LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                                try {
                                    lameta.setColor(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        } else if (meta instanceof BookMeta) {
                            JsonElement titleElement = extraJson.get("title");
                            JsonElement authorElement = extraJson.get("author");
                            JsonElement pagesElement = extraJson.get("pages");

                            BookMeta bmeta = (BookMeta) meta;
                            if (titleElement != null && titleElement.isJsonPrimitive()) {
                                bmeta.setTitle(titleElement.getAsString());
                            }
                            if (authorElement != null && authorElement.isJsonPrimitive()) {
                                bmeta.setAuthor(authorElement.getAsString());
                            }
                            if (pagesElement != null && pagesElement.isJsonArray()) {
                                JsonArray jarray = pagesElement.getAsJsonArray();
                                List<String> pages = new ArrayList<>(jarray.size());
                                jarray.forEach(jsonElement -> {
                                    if (jsonElement.isJsonPrimitive()) pages.add(jsonElement.getAsString());
                                });
                                bmeta.setPages(pages);
                            }

                        } else if (meta instanceof PotionMeta) {
                            JsonElement customEffectsElement = extraJson.get("custom-effects");
                            if (customEffectsElement != null && customEffectsElement.isJsonArray()) {
                                PotionMeta pmeta = (PotionMeta) meta;
                                JsonArray jarray = customEffectsElement.getAsJsonArray();
                                jarray.forEach(jsonElement -> {
                                    if (jsonElement.isJsonPrimitive()) {
                                        String enchantString = jsonElement.getAsString();
                                        if (enchantString.contains(":")) {
                                            try {
                                                String[] splitPotions = enchantString.split(":");
                                                PotionEffectType potionType = PotionEffectType.getByName(splitPotions[0]);
                                                int amplifier = Integer.parseInt(splitPotions[1]);
                                                int duration = Integer.parseInt(splitPotions[2]) * 20;
                                                if (potionType != null) {
                                                    pmeta.addCustomEffect(new PotionEffect(potionType, amplifier, duration), true);
                                                }
                                            } catch (NumberFormatException ignored) {
                                            }
                                        }
                                    }
                                });
                            }
                        } else if (meta instanceof FireworkEffectMeta) {
                            JsonElement effectTypeElement = extraJson.get("type");
                            JsonElement flickerElement = extraJson.get("flicker");
                            JsonElement trailElement = extraJson.get("trail");
                            JsonElement colorsElement = extraJson.get("colors");
                            JsonElement fadeColorsElement = extraJson.get("fade-colors");

                            if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                FireworkEffectMeta femeta = (FireworkEffectMeta) meta;

                                FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeElement.getAsString());

                                List<Color> colors = new ArrayList<>();
                                if (colorsElement != null && colorsElement.isJsonArray())
                                    colorsElement.getAsJsonArray().forEach(colorElement -> {
                                        if (colorElement.isJsonPrimitive())
                                            colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                    });

                                List<Color> fadeColors = new ArrayList<>();
                                if (fadeColorsElement != null && fadeColorsElement.isJsonArray())
                                    fadeColorsElement.getAsJsonArray().forEach(colorElement -> {
                                        if (colorElement.isJsonPrimitive())
                                            fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                    });

                                FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);

                                if (flickerElement != null && flickerElement.isJsonPrimitive())
                                    builder.flicker(flickerElement.getAsBoolean());
                                if (trailElement != null && trailElement.isJsonPrimitive())
                                    builder.trail(trailElement.getAsBoolean());

                                if (!colors.isEmpty()) builder.withColor(colors);
                                if (!fadeColors.isEmpty()) builder.withFade(fadeColors);

                                femeta.setEffect(builder.build());
                            }
                        } else if (meta instanceof FireworkMeta) {
                            FireworkMeta fmeta = (FireworkMeta) meta;

                            JsonElement effectArrayElement = extraJson.get("effects");
                            JsonElement powerElement = extraJson.get("power");

                            if (powerElement != null && powerElement.isJsonPrimitive()) {
                                fmeta.setPower(powerElement.getAsInt());
                            }

                            if (effectArrayElement != null && effectArrayElement.isJsonArray()) {

                                effectArrayElement.getAsJsonArray().forEach(jsonElement -> {
                                    if (jsonElement.isJsonObject()) {

                                        JsonObject jsonObject = jsonElement.getAsJsonObject();

                                        JsonElement effectTypeElement = jsonObject.get("type");
                                        JsonElement flickerElement = jsonObject.get("flicker");
                                        JsonElement trailElement = jsonObject.get("trail");
                                        JsonElement colorsElement = jsonObject.get("colors");
                                        JsonElement fadeColorsElement = jsonObject.get("fade-colors");

                                        if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {

                                            FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeElement.getAsString());

                                            List<Color> colors = new ArrayList<>();
                                            if (colorsElement != null && colorsElement.isJsonArray())
                                                colorsElement.getAsJsonArray().forEach(colorElement -> {
                                                    if (colorElement.isJsonPrimitive())
                                                        colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                });

                                            List<Color> fadeColors = new ArrayList<>();
                                            if (fadeColorsElement != null && fadeColorsElement.isJsonArray())
                                                fadeColorsElement.getAsJsonArray().forEach(colorElement -> {
                                                    if (colorElement.isJsonPrimitive())
                                                        fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                });

                                            FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);

                                            if (flickerElement != null && flickerElement.isJsonPrimitive())
                                                builder.flicker(flickerElement.getAsBoolean());
                                            if (trailElement != null && trailElement.isJsonPrimitive())
                                                builder.trail(trailElement.getAsBoolean());

                                            if (!colors.isEmpty()) builder.withColor(colors);
                                            if (!fadeColors.isEmpty()) builder.withFade(fadeColors);

                                            fmeta.addEffect(builder.build());
                                        }
                                    }
                                });
                            }
                        } else if (meta instanceof MapMeta) {
                            MapMeta mmeta = (MapMeta) meta;

                            JsonElement scalingElement = extraJson.get("scaling");
                            if (scalingElement != null && scalingElement.isJsonPrimitive()) {
                                mmeta.setScaling(scalingElement.getAsBoolean());
                            }

                                /* 1.11
                                JsonElement locationNameElement = extraJson.get("location-name");
                                if(locationNameElement != null && locationNameElement.isJsonPrimitive()) {
                                    mmeta.setLocationName(locationNameElement.getAsString());
                                }

                                JsonElement colorElement = extraJson.get("color");
                                if(colorElement != null && colorElement.isJsonPrimitive()) {
                                    mmeta.setColor(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                }*/
                        }
                    } catch (Exception e) {
                        return null;
                    }
                }
                itemStack.setItemMeta(meta);
            }
            return itemStack;
        } else return null;
    }
}