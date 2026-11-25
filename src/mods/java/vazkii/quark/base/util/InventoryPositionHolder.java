package vazkii.quark.base.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class InventoryPositionHolder {
   public static final int NO_SLOT = -1;
   public static final int MAIN_HAND = -2;
   public static final int OFF_HAND = -3;
   public int slot = -1;
   public ItemStack stack = ItemStack.EMPTY;

   public InventoryPositionHolder() {
   }

   public static ItemStack getStack(EntityPlayer player, int slot) {
      switch(slot) {
      case -3:
         return player.getHeldItemOffhand();
      case -2:
         return player.getHeldItemMainhand();
      case -1:
         return ItemStack.EMPTY;
      default:
         return player.inventory.getStackInSlot(slot);
      }
   }

   public static void setStack(EntityPlayer player, int slot, ItemStack stack) {
      switch(slot) {
      case -3:
         player.setHeldItem(EnumHand.OFF_HAND, stack);
         break;
      case -2:
         player.setHeldItem(EnumHand.MAIN_HAND, stack);
      case -1:
         break;
      default:
         player.inventory.setInventorySlotContents(slot, stack);
      }

   }
}
