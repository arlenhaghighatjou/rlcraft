package com.teammetallurgy.aquaculture.loot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WeightedLootSet {
   public Map<Integer, ItemStack> loot = new HashMap();
   public Map<Integer, Integer> lootMin = new HashMap();
   public Map<Integer, Integer> lootMax = new HashMap();
   public int totalWeight = 0;

   public WeightedLootSet() {
   }

   public void addLoot(Object item, int weight) {
      this.addLoot(item, weight, 1, 1);
   }

   public void addLoot(Object item, int weight, int min, int max) {
      if (item instanceof Item) {
         this.addLoot(new ItemStack((Item)item), weight, min, max);
      } else if (item instanceof Block) {
         this.addLoot(new ItemStack((Block)item), weight, min, max);
      } else if (item instanceof ItemStack) {
         this.addLoot((ItemStack)item, weight, min, max);
      }

   }

   public void addLoot(ItemStack stack, int weight, int min, int max) {
      if (weight > 0 && stack != null) {
         this.loot.put(Integer.valueOf(this.totalWeight + weight), stack);
         this.lootMin.put(Integer.valueOf(this.totalWeight + weight), Integer.valueOf(min));
         this.lootMax.put(Integer.valueOf(this.totalWeight + weight), Integer.valueOf(max));
         this.totalWeight += weight;
      }
   }

   public ItemStack getRandomLoot() {
      EaglercraftRandom rand = new EaglercraftRandom();
      int weight = rand.nextInt(this.totalWeight + 1);
      ItemStack stack = null;
      Set<Integer> keySet = this.loot.keySet();
      Integer[] keys = (Integer[])keySet.toArray(new Integer[keySet.size()]);
      Arrays.sort(keys);

      for(Integer key : keys) {
         if (key.intValue() >= weight) {
            stack = ((ItemStack)this.loot.get(key)).copy();
            int min = ((Integer)this.lootMin.get(key)).intValue();
            int max = ((Integer)this.lootMax.get(key)).intValue();
            int amount = rand.nextInt(max - min + 1) + min;
            stack.setCount(amount);
            if (stack.getItem() == Items.ENCHANTED_BOOK) {
               EnchantmentHelper.addRandomEnchantment(rand, stack, 5 + rand.nextInt(15), true);
            }
            break;
         }
      }

      return stack;
   }
}
