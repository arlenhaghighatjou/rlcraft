package me.lucko.spark.lib.adventure.text.format;

import java.util.function.Function;
import me.lucko.spark.lib.adventure.util.Index;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TextDecoration implements StyleBuilderApplicable, TextFormat {
   OBFUSCATED("obfuscated"),
   BOLD("bold"),
   STRIKETHROUGH("strikethrough"),
   UNDERLINED("underlined"),
   ITALIC("italic");

   public static final Index<String, TextDecoration> NAMES = Index.create(TextDecoration.class, (constant) -> {
      return constant.name;
   });
   private final String name;

   private TextDecoration(String name) {
      this.name = name;
   }

   @NotNull
   public final TextDecorationAndState as(boolean state) {
      return this.as(TextDecoration.State.byBoolean(state));
   }

   @NotNull
   public final TextDecorationAndState as(@NotNull TextDecoration.State state) {
      return new TextDecorationAndStateImpl(this, state);
   }

   public void styleApply(@NotNull Style.Builder style) {
      style.decorate(this);
   }

   @NotNull
   public String toString() {
      return this.name;
   }

   public static enum State {
      NOT_SET("not_set"),
      FALSE("false"),
      TRUE("true");

      private final String name;

      private State(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }

      @NotNull
      public static TextDecoration.State byBoolean(boolean flag) {
         return flag ? TRUE : FALSE;
      }

      @NotNull
      public static TextDecoration.State byBoolean(@Nullable Boolean flag) {
         return flag == null ? NOT_SET : byBoolean(flag.booleanValue());
      }
   }
}
