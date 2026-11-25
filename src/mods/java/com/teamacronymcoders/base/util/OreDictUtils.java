package com.teamacronymcoders.base.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.registrysystem.config.ConfigEntry;
import com.teamacronymcoders.base.registrysystem.config.ConfigEntryBuilder;
import com.teamacronymcoders.base.registrysystem.config.ConfigRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictUtils {
   private static Map<String, ItemStack> preferredItemStacks;
   private static List<String> preferredModIds;
   private static List<String> defaultModIds = Lists.newArrayList(new String[]{"base"});

   private OreDictUtils() {
   }

   public static void addDefaultModId(String modid) {
      addDefaultModId(defaultModIds.size() - 1, modid);
   }

   public static void addDefaultModId(int position, String modid) {
      if (position > defaultModIds.size()) {
         position = defaultModIds.size();
      }

      defaultModIds.add(position, modid);
   }

   public static void setup() {
      preferredItemStacks = Maps.newHashMap();
      preferredModIds = Lists.newArrayList();
      String defaultModIdsString = (String)defaultModIds.stream().collect(Collectors.joining(","));
      ConfigEntry preferredMods = ConfigEntryBuilder.getBuilder("preferredOreDictIds", defaultModIdsString).setArray(true).setCategory("materials").setComment("List for Prioritizing OreDict returns by modid").build();
      ((ConfigRegistry)Base.instance.getRegistry(ConfigRegistry.class, "CONFIG")).addEntry(preferredMods);
      preferredModIds = Arrays.asList(preferredMods.getStringArray());
   }

   @Nonnull
   public static ItemStack getPreferredItemStack(String oreDictName) {
      if (!preferredItemStacks.containsKey(oreDictName)) {
         int bestMatchLevel = preferredModIds.size();
         ItemStack preferredItemStack = ItemStack.EMPTY;
         List<ItemStack> itemStackList = OreDictionary.getOres(oreDictName);

         for(ItemStack itemStack : itemStackList) {
            if (ItemStackUtils.isValid(itemStack)) {
               String modid = ItemStackUtils.getModIdFromItemStack(itemStack);
               if (preferredModIds.contains(modid)) {
                  int newMatch = preferredModIds.indexOf(modid);
                  bestMatchLevel = newMatch < bestMatchLevel ? newMatch : bestMatchLevel;
                  preferredItemStack = itemStack;
               }
            }
         }

         if (preferredItemStack.isEmpty() && !itemStackList.isEmpty()) {
            preferredItemStack = (ItemStack)itemStackList.get(0);
         }

         preferredItemStacks.put(oreDictName, preferredItemStack);
      }

      ItemStack itemStack = (ItemStack)preferredItemStacks.get(oreDictName);
      return ItemStackUtils.isValid(itemStack) ? itemStack.copy() : ItemStack.EMPTY;
   }
}
