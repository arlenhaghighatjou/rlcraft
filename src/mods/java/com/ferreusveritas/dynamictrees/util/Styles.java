package com.ferreusveritas.dynamictrees.util;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public final class Styles {
   public static final Style ERROR = new Style() {
      public TextFormatting getColor() {
         return TextFormatting.RED;
      }
   };

   public Styles() {
   }
}
