package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public final class ItemStackSnapshot {
   public ItemStack savedstack;
   public int iindex;

   public ItemStackSnapshot(@Nullable ItemStack original, int i) {
      this.iindex = i;
      this.savedstack = original == null ? ItemStacks.NULLSTACK() : original.copy();
   }
}
