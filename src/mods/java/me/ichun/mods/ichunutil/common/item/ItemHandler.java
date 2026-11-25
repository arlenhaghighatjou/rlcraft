package me.ichun.mods.ichunutil.common.item;

import java.util.HashMap;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public class ItemHandler {
   public static HashMap<Class<? extends Item>, DualHandedItemCallback> dualHandedItems = new HashMap();

   public ItemHandler() {
   }

   public static void registerDualHandedItem(@Nonnull Class<? extends Item> clz, DualHandedItemCallback itemCallback) {
      dualHandedItems.put(clz, itemCallback != null ? itemCallback : DualHandedItemCallback.DEFAULT);
   }

   public static void registerDualHandedItem(@Nonnull Class<? extends Item> clz) {
      registerDualHandedItem(clz, (DualHandedItemCallback)null);
   }

   public static DualHandedItemCallback getDualHandedItemCallback(ItemStack is) {
      for(Entry<Class<? extends Item>, DualHandedItemCallback> e : dualHandedItems.entrySet()) {
         if (((Class)e.getKey()).isInstance(is.getItem())) {
            return (DualHandedItemCallback)e.getValue();
         }
      }

      return DualHandedItemCallback.DEFAULT;
   }

   public static boolean isItemDualHanded(ItemStack is) {
      if (is.isEmpty()) {
         return false;
      } else {
         for(Entry<Class<? extends Item>, DualHandedItemCallback> e : dualHandedItems.entrySet()) {
            if (((Class)e.getKey()).isInstance(is.getItem())) {
               return ((DualHandedItemCallback)e.getValue()).isItemDualHanded(is);
            }
         }

         return false;
      }
   }

   public static boolean canItemBeUsed(EntityLivingBase living, ItemStack is) {
      return is != null && (!isItemDualHanded(is) || getDualHandedItemCallback(is).canItemBeUsed(is, living) && (living.getHeldItem(EnumHand.MAIN_HAND) == is && living.getHeldItem(EnumHand.OFF_HAND).isEmpty() || living.getHeldItem(EnumHand.OFF_HAND) == is && living.getHeldItem(EnumHand.MAIN_HAND).isEmpty()));
   }

   @Nonnull
   public static ItemStack getUsableDualHandedItem(EntityLivingBase living) {
      ItemStack is = living.getHeldItem(EnumHand.MAIN_HAND);
      if (isItemDualHanded(is) && canItemBeUsed(living, is)) {
         return is;
      } else {
         is = living.getHeldItem(EnumHand.OFF_HAND);
         return isItemDualHanded(is) && canItemBeUsed(living, is) ? is : ItemStack.EMPTY;
      }
   }

   public static EnumHandSide getHandSide(EntityLivingBase living, ItemStack is) {
      return living.getHeldItem(EnumHand.OFF_HAND) == is ? living.getPrimaryHand().opposite() : living.getPrimaryHand();
   }
}
