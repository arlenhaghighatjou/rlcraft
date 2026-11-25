package com.oblivioussp.spartanhudbaubles.util;

public enum SkinType {
   NONE,
   HOTBAR_STYLE,
   TOOLTIP_STYLE,
   MINIMAL;

   private SkinType() {
   }

   public static String[] getValidConfigValues() {
      SkinType[] validValues = values();
      String[] validStrings = new String[validValues.length];

      for(int i = 0; i < validValues.length; ++i) {
         SkinType value = validValues[i];
         validStrings[i] = value.toString();
      }

      return validStrings;
   }
}
