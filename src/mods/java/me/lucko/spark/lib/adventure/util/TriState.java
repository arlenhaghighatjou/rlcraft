package me.lucko.spark.lib.adventure.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TriState {
   NOT_SET,
   FALSE,
   TRUE;

   private TriState() {
   }

   @NotNull
   public static TriState byBoolean(boolean value) {
      return value ? TRUE : FALSE;
   }

   @NotNull
   public static TriState byBoolean(@Nullable Boolean value) {
      return value == null ? NOT_SET : byBoolean(value.booleanValue());
   }
}
