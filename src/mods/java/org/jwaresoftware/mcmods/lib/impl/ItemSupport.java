package org.jwaresoftware.mcmods.lib.impl;

import javax.annotation.Nonnull;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jwaresoftware.mcmods.lib.Instructions;

public class ItemSupport {
   public static final boolean isPristine(@Nonnull ItemStack stack, @Nonnull Item singleton) {
      return stack.getItem() == singleton && !stack.isItemEnchanted() && !Instructions.present(stack);
   }

   public static final boolean isPristine(@Nonnull ItemStack stack) {
      return !stack.isItemEnchanted() && !Instructions.present(stack);
   }

   public static final void noteItemConsumed(EntityPlayer player, ItemStack stack) {
      if (player instanceof EntityPlayerMP) {
         CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)player, stack);
      }

   }

   protected ItemSupport() {
   }
}
