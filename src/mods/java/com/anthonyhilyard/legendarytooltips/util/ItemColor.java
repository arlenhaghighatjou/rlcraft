package com.anthonyhilyard.legendarytooltips.util;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;

public abstract class ItemColor {
   public ItemColor() {
   }

   public static Integer findFirstColorCode(String rawTitle) {
      for(int i = 0; i < rawTitle.length(); i += 2) {
         if (rawTitle.charAt(i) != 167) {
            return null;
         }

         try {
            String format = rawTitle.charAt(i) + String.valueOf(rawTitle.charAt(i + 1));
            Integer color = TextColor.getColorFromFormatCode(format);
            if (color != null) {
               return color;
            }
         } catch (StringIndexOutOfBoundsException var4) {
            return null;
         }
      }

      return null;
   }

   public static Integer getColorForItem(ItemStack item, int defaultColor) {
      Integer result = null;
      if (item == null) {
         return defaultColor;
      } else {
         if (item.getTextComponent() != null && item.getTextComponent().getStyle() != null && item.getTextComponent().getStyle().getColor() != null) {
            result = TextColor.getColorFromFormatCode(item.getTextComponent().getStyle().getColor().toString());
         }

         if (item.hasTagCompound() && item.getTagCompound().hasKey("display", 10) && item.getTagCompound().getCompoundTag("display").hasKey("color", 3)) {
            result = item.getTagCompound().getCompoundTag("display").getInteger("color");
         }

         if (result == null) {
            Minecraft mc = Minecraft.getMinecraft();
            List<String> lines = item.getTooltip(mc.player, TooltipFlags.ADVANCED);
            if (!lines.isEmpty()) {
               result = findFirstColorCode((String)lines.get(0));
            }
         }

         if (result == null) {
            result = defaultColor;
         }

         return result;
      }
   }
}
