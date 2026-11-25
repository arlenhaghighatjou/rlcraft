package ivorius.ivtoolkit.tools;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IvInventoryHelper {
   public IvInventoryHelper() {
   }

   public static boolean consumeInventoryItem(InventoryPlayer inventory, ItemStack itemStack) {
      int var2 = getInventorySlotContainItem(inventory, itemStack);
      if (var2 < 0) {
         return false;
      } else {
         ((ItemStack)inventory.mainInventory.get(var2)).setCount(((ItemStack)inventory.mainInventory.get(var2)).getCount() - 1);
         return true;
      }
   }

   public static int getInventorySlotContainItem(InventoryPlayer inventory, ItemStack itemStack) {
      for(int var2 = 0; var2 < inventory.mainInventory.size(); ++var2) {
         if (!((ItemStack)inventory.mainInventory.get(var2)).isEmpty() && ((ItemStack)inventory.mainInventory.get(var2)).isItemEqual(itemStack)) {
            return var2;
         }
      }

      return -1;
   }

   public static int getInventorySlotContainItem(InventoryPlayer inventory, Item item) {
      for(int var2 = 0; var2 < inventory.mainInventory.size(); ++var2) {
         if (!((ItemStack)inventory.mainInventory.get(var2)).isEmpty() && ((ItemStack)inventory.mainInventory.get(var2)).getItem() == item) {
            return var2;
         }
      }

      return -1;
   }
}
