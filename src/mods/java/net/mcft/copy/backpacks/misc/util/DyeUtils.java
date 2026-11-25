package net.mcft.copy.backpacks.misc.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class DyeUtils {
   private static final Map<String, Integer> dyes = new HashMap();

   private DyeUtils() {
   }

   public static int getDyeColor(ItemStack stack) {
      if (stack.isEmpty()) {
         return -1;
      } else {
         int[] oreIds = OreDictionary.getOreIDs(stack);

         for(int ore : oreIds) {
            String name = OreDictionary.getOreName(ore);
            Integer color = (Integer)dyes.get(name);
            if (color != null) {
               return color.intValue();
            }
         }

         return -1;
      }
   }

   public static boolean isDye(ItemStack stack) {
      return getDyeColor(stack) >= 0;
   }

   public static int getColorFromDyes(int color, Collection<ItemStack> dyes) {
      int number = dyes.size();
      if (number < 1) {
         return color;
      } else {
         int r = 0;
         int g = 0;
         int b = 0;
         if (color >= 0) {
            r = color >> 16;
            g = color >> 8 & 255;
            b = color & 255;
            ++number;
         }

         for(ItemStack dye : dyes) {
            color = getDyeColor(dye);
            if (color >= 0) {
               r += color >> 16;
               g += color >> 8 & 255;
               b += color & 255;
            }
         }

         r = r / number;
         g = g / number;
         b = b / number;
         return r << 16 | g << 8 | b;
      }
   }

   public static int getColorFromDyes(Collection<ItemStack> dyes) {
      return getColorFromDyes(-1, dyes);
   }

   static {
      String[] nameLookup = new String[]{"Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White"};

      for(EnumDyeColor color : EnumDyeColor.values()) {
         String name = "dye" + nameLookup[color.getDyeDamage()];
         float[] values = color.getColorComponentValues();
         int r = (int)(values[0] * 255.0F);
         int g = (int)(values[1] * 255.0F);
         int b = (int)(values[2] * 255.0F);
         dyes.put(name, Integer.valueOf(r << 16 | g << 8 | b));
      }

   }
}
