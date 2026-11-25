package net.silentchaos512.scalinghealth.utils;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;

/** @deprecated */
@Deprecated
public class StackProducer {
   public ItemStack stack;

   protected StackProducer() {
      this(ItemStack.EMPTY);
   }

   public StackProducer(ItemStack stack) {
      this.stack = stack;
   }

   public ItemStack get(EaglercraftRandom rand) {
      return this.stack;
   }
}
