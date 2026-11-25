package mezz.jei.api;

import net.minecraft.item.ItemStack;

/** @deprecated */
@Deprecated
public interface IItemBlacklist {
   /** @deprecated */
   @Deprecated
   void addItemToBlacklist(ItemStack var1);

   /** @deprecated */
   @Deprecated
   void removeItemFromBlacklist(ItemStack var1);

   /** @deprecated */
   @Deprecated
   boolean isItemBlacklisted(ItemStack var1);
}
