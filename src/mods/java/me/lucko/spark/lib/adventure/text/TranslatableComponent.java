package me.lucko.spark.lib.adventure.text;

import java.util.List;
import java.util.Objects;
import me.lucko.spark.lib.adventure.translation.Translatable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface TranslatableComponent extends BuildableComponent<TranslatableComponent, TranslatableComponent.Builder>, ScopedComponent<TranslatableComponent> {
   @NotNull
   String key();

   @Contract(
      pure = true
   )
   @NotNull
   default TranslatableComponent key(@NotNull Translatable translatable) {
      return this.key(((Translatable)Objects.requireNonNull(translatable, "translatable")).translationKey());
   }

   @Contract(
      pure = true
   )
   @NotNull
   TranslatableComponent key(@NotNull String var1);

   @NotNull
   List<Component> args();

   @Contract(
      pure = true
   )
   @NotNull
   TranslatableComponent args(@NotNull ComponentLike... var1);

   @Contract(
      pure = true
   )
   @NotNull
   TranslatableComponent args(@NotNull List<? extends ComponentLike> var1);

   public interface Builder extends ComponentBuilder<TranslatableComponent, TranslatableComponent.Builder> {
      @Contract(
         pure = true
      )
      @NotNull
      default TranslatableComponent.Builder key(@NotNull Translatable translatable) {
         return this.key(((Translatable)Objects.requireNonNull(translatable, "translatable")).translationKey());
      }

      @Contract("_ -> this")
      @NotNull
      TranslatableComponent.Builder key(@NotNull String var1);

      @Contract("_ -> this")
      @NotNull
      TranslatableComponent.Builder args(@NotNull ComponentBuilder<?, ?> var1);

      @Contract("_ -> this")
      @NotNull
      TranslatableComponent.Builder args(@NotNull ComponentBuilder<?, ?>... var1);

      @Contract("_ -> this")
      @NotNull
      TranslatableComponent.Builder args(@NotNull Component var1);

      @Contract("_ -> this")
      @NotNull
      TranslatableComponent.Builder args(@NotNull ComponentLike... var1);

      @Contract("_ -> this")
      @NotNull
      TranslatableComponent.Builder args(@NotNull List<? extends ComponentLike> var1);
   }
}
