package net.silentchaos512.lib.util;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class DyeHelper {
   public DyeHelper() {
   }

   public static String getOreName(EnumDyeColor dye) {
      String name = dye.getUnlocalizedName();
      return "dye" + (dye == EnumDyeColor.SILVER ? "LightGray" : Character.toUpperCase(name.charAt(0)) + name.substring(1));
   }

   public static int getColor(EnumDyeColor dye) {
      return ItemDye.DYE_COLORS[dye.getDyeDamage()];
   }

   public static boolean isItemDye(ItemStack stack) {
      return !oreDictDyeToVanilla(stack).isEmpty();
   }

   public static ItemStack oreDictDyeToVanilla(ItemStack stack) {
      for(int id : OreDictionary.getOreIDs(stack)) {
         String name = OreDictionary.getOreName(id);
         if (name.startsWith("dye")) {
            name = name.substring(3);
            if ("lightgray".equals(name)) {
               name = "silver";
            }

            for(int i = 0; i < EnumDyeColor.values().length; ++i) {
               if (EnumDyeColor.values()[i].getUnlocalizedName().equalsIgnoreCase(name)) {
                  return new ItemStack(Items.DYE, 1, i);
               }
            }
         }
      }

      return ItemStack.EMPTY;
   }
}
