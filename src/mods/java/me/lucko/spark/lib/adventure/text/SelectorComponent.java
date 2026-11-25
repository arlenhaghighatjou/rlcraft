package me.lucko.spark.lib.adventure.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectorComponent extends BuildableComponent<SelectorComponent, SelectorComponent.Builder>, ScopedComponent<SelectorComponent> {
   @NotNull
   String pattern();

   @Contract(
      pure = true
   )
   @NotNull
   SelectorComponent pattern(@NotNull String var1);

   @Nullable
   Component separator();

   @NotNull
   SelectorComponent separator(@Nullable ComponentLike var1);

   public interface Builder extends ComponentBuilder<SelectorComponent, SelectorComponent.Builder> {
      @Contract("_ -> this")
      @NotNull
      SelectorComponent.Builder pattern(@NotNull String var1);

      @Contract("_ -> this")
      @NotNull
      SelectorComponent.Builder separator(@Nullable ComponentLike var1);
   }
}
