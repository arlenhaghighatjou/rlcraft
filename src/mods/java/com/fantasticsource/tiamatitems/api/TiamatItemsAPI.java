package com.fantasticsource.tiamatitems.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;

public class TiamatItemsAPI {
   private static ITiamatItemsNatives tiamatItemsAPIMethods = null;

   public TiamatItemsAPI() {
   }

   public static boolean isUsable(ItemStack stack) {
      return tiamatItemsAPIMethods == null || tiamatItemsAPIMethods.isUsable(stack);
   }

   public static ArrayList<IPartSlot> getPartSlots(ItemStack stack) {
      return tiamatItemsAPIMethods == null ? null : tiamatItemsAPIMethods.getPartSlots(stack);
   }

   static {
      try {
         for(Field field : Class.forName("com.fantasticsource.tiamatitems.apinatives.TiamatItemsNatives").getDeclaredFields()) {
            if (field.getName().equals("NATIVES")) {
               tiamatItemsAPIMethods = (ITiamatItemsNatives)field.get((Object)null);
            }
         }
      } catch (IllegalAccessException | ClassNotFoundException var4) {
         var4.printStackTrace();
      }

   }
}
