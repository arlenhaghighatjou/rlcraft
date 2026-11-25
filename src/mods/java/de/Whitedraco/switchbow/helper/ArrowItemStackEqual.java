package de.Whitedraco.switchbow.helper;

import java.util.List;
import java.util.Vector;
import net.minecraft.item.ItemStack;

public class ArrowItemStackEqual {
   public ArrowItemStackEqual() {
   }

   public static boolean containsArrow(Vector<ItemStack> arrows, ItemStack comparison) {
      for(int a = 0; a < arrows.size(); ++a) {
         if (ItemStack.areItemStackTagsEqual((ItemStack)arrows.get(a), comparison) && ItemStack.areItemsEqual((ItemStack)arrows.get(a), comparison)) {
            if (ItemStack.areItemStackTagsEqual((ItemStack)arrows.get(a), comparison)) {
               if (ItemStack.areItemsEqual((ItemStack)arrows.get(a), comparison)) {
                  return true;
               }

               return false;
            }

            if (ItemStack.areItemsEqual((ItemStack)arrows.get(a), comparison)) {
               if (ItemStack.areItemStackTagsEqual((ItemStack)arrows.get(a), comparison)) {
                  return true;
               }

               return false;
            }
         }
      }

      return false;
   }

   public static boolean containsArrow(List<ItemStack> arrows, ItemStack comparison) {
      for(int a = 0; a < arrows.size(); ++a) {
         if (ItemStack.areItemStackTagsEqual((ItemStack)arrows.get(a), comparison) && ItemStack.areItemsEqual((ItemStack)arrows.get(a), comparison)) {
            if (ItemStack.areItemStackTagsEqual((ItemStack)arrows.get(a), comparison)) {
               if (ItemStack.areItemsEqual((ItemStack)arrows.get(a), comparison)) {
                  return true;
               }

               return false;
            }

            if (ItemStack.areItemsEqual((ItemStack)arrows.get(a), comparison)) {
               if (ItemStack.areItemStackTagsEqual((ItemStack)arrows.get(a), comparison)) {
                  return true;
               }

               return false;
            }
         }
      }

      return false;
   }

   public static boolean containsArrow(ItemStack arrows, ItemStack comparison) {
      if (ItemStack.areItemStackTagsEqual(arrows, comparison) && ItemStack.areItemsEqual(arrows, comparison)) {
         if (ItemStack.areItemStackTagsEqual(arrows, comparison)) {
            if (ItemStack.areItemsEqual(arrows, comparison)) {
               return true;
            }

            return false;
         }

         if (ItemStack.areItemsEqual(arrows, comparison)) {
            if (ItemStack.areItemStackTagsEqual(arrows, comparison)) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
