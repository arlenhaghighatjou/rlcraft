package me.lucko.spark.lib.adventure.text.format;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Debug.Renderer;

@Renderer(
   text = "asHexString()"
)
final class TextColorImpl implements TextColor {
   private final int value;

   TextColorImpl(int value) {
      this.value = value;
   }

   public int value() {
      return this.value;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof TextColorImpl)) {
         return false;
      } else {
         TextColorImpl that = (TextColorImpl)other;
         return this.value == that.value;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public String toString() {
      return this.asHexString();
   }
}
