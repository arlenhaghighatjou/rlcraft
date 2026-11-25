package net.silentchaos512.scalinghealth.utils;

import java.util.Random;
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

   public ItemStack get(Random rand) {
      return this.stack;
   }
}
