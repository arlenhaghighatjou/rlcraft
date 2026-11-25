package me.lucko.spark.lib.adventure.util;

import org.jetbrains.annotations.NotNull;

public abstract class Nag extends RuntimeException {
   public static void print(@NotNull Nag nag) {
      nag.printStackTrace();
   }

   protected Nag(String message) {
      super(message);
   }
}
