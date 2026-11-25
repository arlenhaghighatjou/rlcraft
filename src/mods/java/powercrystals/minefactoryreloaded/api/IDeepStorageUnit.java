package powercrystals.minefactoryreloaded.api;

import net.minecraft.item.ItemStack;

public interface IDeepStorageUnit {
   ItemStack getStoredItemType();

   void setStoredItemCount(int var1);

   void setStoredItemType(ItemStack var1, int var2);

   int getMaxStoredCount();
}
