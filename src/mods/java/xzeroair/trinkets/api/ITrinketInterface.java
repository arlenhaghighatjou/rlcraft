package xzeroair.trinkets.api;

import net.minecraft.item.ItemStack;

public interface ITrinketInterface {
   default String getAccessoryType() {
      return "trinket";
   }

   int getSlot(ItemStack var1);

   String getItemHandler(ItemStack var1);
}
