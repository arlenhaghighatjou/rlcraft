package com.teammetallurgy.aquaculture.enchantments;

public class RomanNumeral {
   public RomanNumeral() {
   }

   public static String convertToRoman(int i) {
      if (i == 1) {
         return " I";
      } else if (i == 2) {
         return " II";
      } else if (i == 3) {
         return " III";
      } else if (i == 4) {
         return " IV";
      } else {
         return i == 5 ? " V" : "";
      }
   }
}
