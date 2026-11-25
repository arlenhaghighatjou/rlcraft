package codersafterdark.compatskills.common.compats.minecraft.drops;

import codersafterdark.reskillable.api.data.LockKey;
import net.minecraft.item.ItemStack;

public class ItemStackDropKey implements LockKey {
   private final ItemStack stack;

   public ItemStackDropKey(ItemStack stack) {
      this.stack = stack;
   }

   public boolean equals(Object obj) {
      return obj == this || obj instanceof ItemStackDropKey && ItemStack.areItemStacksEqual(this.stack, ((ItemStackDropKey)obj).stack);
   }

   public int hashCode() {
      return this.stack.getItem().hashCode();
   }
}
