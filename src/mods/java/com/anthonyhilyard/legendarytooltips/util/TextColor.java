package com.anthonyhilyard.legendarytooltips.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public abstract class TextColor {
   public TextColor() {
   }

   public static Integer getColorFromFormatCode(String formatCode) {
      Integer color = Minecraft.getMinecraft().fontRenderer.getColorCode(formatCode.charAt(1));
      if (color.intValue() == -1) {
         color = null;
      }

      return color;
   }

   public static Integer parseColor(String string) {
      if (string.startsWith("#")) {
         try {
            return Integer.parseUnsignedInt(string.substring(1), 16);
         } catch (NumberFormatException var2) {
            return null;
         }
      } else {
         TextFormatting namedColor = TextFormatting.getValueByName(string);
         return namedColor != null && namedColor.isColor() ? getColorFromFormatCode(namedColor.toString()) : null;
      }
   }
}
