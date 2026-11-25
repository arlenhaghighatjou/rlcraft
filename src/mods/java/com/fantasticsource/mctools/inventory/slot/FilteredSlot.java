package com.fantasticsource.mctools.inventory.slot;

import java.util.function.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FilteredSlot extends BetterSlot {
   public final Predicate<ItemStack> filter;
   public final boolean bindingCursable;
   public final int slotStackLimit;

   public FilteredSlot(IInventory inventoryIn, int index, int x, int y, ResourceLocation texture, int texWidth16x, int texHeight16x, int u, int v, boolean bindingCursable, int slotStackLimit, Predicate<ItemStack> filter) {
      super(inventoryIn, index, x, y, texture, texWidth16x, texHeight16x, u, v);
      this.filter = filter;
      this.bindingCursable = bindingCursable;
      this.slotStackLimit = slotStackLimit;
   }

   public boolean isItemValid(ItemStack stack) {
      return this.filter.test(stack);
   }

   public boolean canTakeStack(EntityPlayer player) {
      if (this.bindingCursable && !player.isCreative()) {
         ItemStack itemstack = this.getStack();
         return itemstack.isEmpty() || !EnchantmentHelper.hasBindingCurse(itemstack);
      } else {
         return true;
      }
   }

   public int getSlotStackLimit() {
      return this.slotStackLimit;
   }
}
