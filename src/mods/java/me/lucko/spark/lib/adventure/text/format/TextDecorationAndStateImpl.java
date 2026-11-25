package me.lucko.spark.lib.adventure.text.format;

import me.lucko.spark.lib.adventure.examination.string.StringExaminer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class TextDecorationAndStateImpl implements TextDecorationAndState {
   private final TextDecoration decoration;
   private final TextDecoration.State state;

   TextDecorationAndStateImpl(TextDecoration decoration, TextDecoration.State state) {
      this.decoration = decoration;
      this.state = state;
   }

   @NonNull
   public TextDecoration decoration() {
      return this.decoration;
   }

   @NonNull
   public TextDecoration.State state() {
      return this.state;
   }

   public String toString() {
      return (String)this.examine(StringExaminer.simpleEscaping());
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         TextDecorationAndStateImpl that = (TextDecorationAndStateImpl)other;
         return this.decoration == that.decoration && this.state == that.state;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.decoration.hashCode();
      result = 31 * result + this.state.hashCode();
      return result;
   }
}
