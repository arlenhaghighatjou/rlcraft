package com.endertech.minecraft.forge.data;

import com.endertech.common.CommonString;

public final class Names {
   public Names() {
   }

   public static CommonString.Joiner dotted() {
      return CommonString.Joiner.with(".");
   }

   public static CommonString.Joiner registry() {
      return CommonString.Joiner.with("_");
   }

   public static CommonString.Joiner localized() {
      return CommonString.Joiner.with(" ");
   }
}
