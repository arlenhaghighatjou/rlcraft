package com.anthonyhilyard.legendarytooltips.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class Selectors {
   private static final int TAG_COMPOUND = 10;
   private static final int TAG_LIST = 9;
   private static Map<String, EnumRarity> rarities = new HashMap<String, EnumRarity>() {
      {
         this.put("common", EnumRarity.COMMON);
         this.put("uncommon", EnumRarity.UNCOMMON);
         this.put("rare", EnumRarity.RARE);
         this.put("epic", EnumRarity.EPIC);
      }
   };
   private static Map<String, BiPredicate<NBTBase, String>> nbtComparators = new HashMap<String, BiPredicate<NBTBase, String>>() {
      {
         this.put("=", (tag, value) -> {
            return tag.toString().contentEquals(value);
         });
         this.put("!=", (tag, value) -> {
            return !tag.toString().contentEquals(value);
         });
         this.put(">", (tag, value) -> {
            try {
               double parsedValue = Double.valueOf(value).doubleValue();
               if (tag instanceof NBTPrimitive) {
                  return ((NBTPrimitive)tag).getDouble() > parsedValue;
               } else {
                  return false;
               }
            } catch (Exception var4) {
               return false;
            }
         });
         this.put("<", (tag, value) -> {
            try {
               double parsedValue = Double.valueOf(value).doubleValue();
               if (tag instanceof NBTPrimitive) {
                  return ((NBTPrimitive)tag).getDouble() < parsedValue;
               } else {
                  return false;
               }
            } catch (Exception var4) {
               return false;
            }
         });
      }
   };

   public Selectors() {
   }

   public static boolean itemMatches(ItemStack item, String selector) {
      String itemResourceLocation = ForgeRegistries.ITEMS.getKey(item.getItem()).toString();
      int metadata = -1;
      String[] chunks = selector.split(":");
      if (chunks.length > 1) {
         try {
            metadata = Integer.parseInt(chunks[chunks.length - 1]);
            selector = selector.substring(0, selector.length() - chunks[chunks.length - 1].length() - 1);
         } catch (NumberFormatException var11) {
            ;
         }
      }

      if (!selector.equals(itemResourceLocation) && !selector.equals(itemResourceLocation.replace("minecraft:", "")) || metadata != -1 && metadata != item.getMetadata()) {
         if (selector.startsWith("#")) {
            Integer entryColor = TextColor.parseColor(selector);
            if (entryColor != null && entryColor.equals(ItemColor.getColorForItem(item, 16777215))) {
               return true;
            }
         } else if (selector.startsWith("!")) {
            if (item.getItem().getForgeRarity(item) == rarities.get(selector.substring(1))) {
               return true;
            }
         } else if (selector.startsWith("@")) {
            if (itemResourceLocation.startsWith(selector.substring(1) + ":")) {
               return true;
            }
         } else if (selector.startsWith("$")) {
            if (OreDictionary.containsMatch(false, OreDictionary.getOres(selector.substring(1)), new ItemStack[]{item})) {
               return true;
            }
         } else if (selector.startsWith("%")) {
            if (item.getDisplayName().contains(selector.substring(1))) {
               return true;
            }
         } else if (selector.startsWith("^")) {
            Minecraft mc = Minecraft.getMinecraft();
            List<String> lines = item.getTooltip(mc.player, TooltipFlags.ADVANCED);
            String tooltipText = "";

            for(int n = 1; n < lines.size(); ++n) {
               tooltipText = tooltipText + (String)lines.get(n) + '\n';
            }

            if (tooltipText.contains(selector.substring(1))) {
               return true;
            }
         } else if (selector.startsWith("&")) {
            String tagName = selector.substring(1);
            String tagValue = null;
            BiPredicate<NBTBase, String> valueChecker = null;

            for(String comparator : nbtComparators.keySet()) {
               if (tagName.contains(comparator)) {
                  valueChecker = (BiPredicate)nbtComparators.get(comparator);
                  String[] components = tagName.split(comparator);
                  tagName = components[0];
                  if (components.length > 1) {
                     tagValue = components[1];
                  }
                  break;
               }
            }

            return findMatchingSubtag(item.getTagCompound(), tagName, tagValue, valueChecker);
         }

         return false;
      } else {
         return true;
      }
   }

   private static boolean findMatchingSubtag(NBTBase tag, String key, String value, BiPredicate<NBTBase, String> valueChecker) {
      if (tag == null) {
         return false;
      } else if (tag.getId() == 10) {
         NBTTagCompound compoundTag = (NBTTagCompound)tag;
         if (compoundTag.hasKey(key)) {
            return value == null && valueChecker == null ? true : valueChecker.test(compoundTag.getTag(key), value);
         } else {
            Iterator var8 = compoundTag.getKeySet().iterator();

            while(true) {
               if (!var8.hasNext()) {
                  return false;
               }

               String innerKey = (String)var8.next();
               if ((compoundTag.getTagId(innerKey) == 9 || compoundTag.getTagId(innerKey) == 10) && findMatchingSubtag(compoundTag.getTag(innerKey), key, value, valueChecker)) {
                  break;
               }
            }

            return true;
         }
      } else {
         if (tag.getId() == 9) {
            for(NBTBase innerTag : (NBTTagList)tag) {
               if ((innerTag.getId() == 9 || innerTag.getId() == 10) && findMatchingSubtag(innerTag, key, value, valueChecker)) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
