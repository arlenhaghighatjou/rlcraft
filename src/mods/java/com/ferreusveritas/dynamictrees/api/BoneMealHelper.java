package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.TriPredicate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BoneMealHelper {
   private static final Map<ItemStack, TriPredicate<World, BlockPos, Species>> boneMealItems = new HashMap();

   public BoneMealHelper() {
   }

   public static void addItem(Item item, int meta) {
      addItem(new ItemStack(item, 1, meta));
   }

   public static void addItem(ItemStack stack) {
      addItem(stack, (w, b, s) -> {
         return true;
      });
   }

   public static void addItem(ItemStack stack, TriPredicate<World, BlockPos, Species> predicate) {
      stack.setCount(1);
      boneMealItems.put(stack, predicate);
   }

   public static void replacePredicate(ItemStack stack, TriPredicate<World, BlockPos, Species> predicate) {
      for(Entry<ItemStack, TriPredicate<World, BlockPos, Species>> entry : boneMealItems.entrySet()) {
         if (((ItemStack)entry.getKey()).isItemEqual(stack)) {
            entry.setValue(predicate);
            return;
         }
      }

   }

   public static boolean isBoneMeal(ItemStack stack, World world, BlockPos blockPos, Species species) {
      for(Entry<ItemStack, TriPredicate<World, BlockPos, Species>> entry : boneMealItems.entrySet()) {
         if (((ItemStack)entry.getKey()).isItemEqual(stack) && ((TriPredicate)entry.getValue()).test(world, blockPos, species)) {
            return true;
         }
      }

      return false;
   }
}
