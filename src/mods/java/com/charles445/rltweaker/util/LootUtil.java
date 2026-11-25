package com.charles445.rltweaker.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootUtil {
   public LootUtil() {
   }

   public static class DoNothingFunction extends LootFunction {
      public DoNothingFunction() {
         this(new LootCondition[0]);
      }

      public DoNothingFunction(LootCondition[] conditionsIn) {
         super(conditionsIn);
      }

      public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
         return stack;
      }
   }

   public static class FailCondition implements LootCondition {
      public FailCondition() {
      }

      public boolean testCondition(EaglercraftRandom rand, LootContext context) {
         return false;
      }
   }
}
