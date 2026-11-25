package me.lucko.spark.lib.adventure.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ScoreComponent extends BuildableComponent<ScoreComponent, ScoreComponent.Builder>, ScopedComponent<ScoreComponent> {
   @NotNull
   String name();

   @Contract(
      pure = true
   )
   @NotNull
   ScoreComponent name(@NotNull String var1);

   @NotNull
   String objective();

   @Contract(
      pure = true
   )
   @NotNull
   ScoreComponent objective(@NotNull String var1);

   /** @deprecated */
   @Deprecated
   @Nullable
   String value();

   /** @deprecated */
   @Deprecated
   @Contract(
      pure = true
   )
   @NotNull
   ScoreComponent value(@Nullable String var1);

   public interface Builder extends ComponentBuilder<ScoreComponent, ScoreComponent.Builder> {
      @Contract("_ -> this")
      @NotNull
      ScoreComponent.Builder name(@NotNull String var1);

      @Contract("_ -> this")
      @NotNull
      ScoreComponent.Builder objective(@NotNull String var1);

      /** @deprecated */
      @Deprecated
      @Contract("_ -> this")
      @NotNull
      ScoreComponent.Builder value(@Nullable String var1);
   }
}
