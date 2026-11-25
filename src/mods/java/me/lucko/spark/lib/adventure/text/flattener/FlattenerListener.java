package me.lucko.spark.lib.adventure.text.flattener;

import me.lucko.spark.lib.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FlattenerListener {
   default void pushStyle(@NotNull Style style) {
   }

   void component(@NotNull String var1);

   default void popStyle(@NotNull Style style) {
   }
}
