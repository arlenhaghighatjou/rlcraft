package me.lucko.spark.lib.adventure.text;

import java.util.Arrays;
import me.lucko.spark.lib.adventure.text.format.Style;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface TextComponent extends BuildableComponent<TextComponent, TextComponent.Builder>, ScopedComponent<TextComponent> {
   @NotNull
   static TextComponent ofChildren(@NotNull ComponentLike... components) {
      return (TextComponent)(components.length == 0 ? Component.empty() : new TextComponentImpl(Arrays.asList(components), Style.empty(), ""));
   }

   @NotNull
   String content();

   @Contract(
      pure = true
   )
   @NotNull
   TextComponent content(@NotNull String var1);

   public interface Builder extends ComponentBuilder<TextComponent, TextComponent.Builder> {
      @NotNull
      String content();

      @Contract("_ -> this")
      @NotNull
      TextComponent.Builder content(@NotNull String var1);
   }
}
