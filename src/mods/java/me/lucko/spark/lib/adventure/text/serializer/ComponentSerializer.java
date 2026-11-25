package me.lucko.spark.lib.adventure.text.serializer;

import me.lucko.spark.lib.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

public interface ComponentSerializer<I extends Component, O extends Component, R> {
   @NotNull
   O deserialize(@NotNull R var1);

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval
   @Contract(
      value = "!null -> !null; null -> null",
      pure = true
   )
   @Nullable
   default O deseializeOrNull(@Nullable R input) {
      return (O)this.deserializeOrNull(input);
   }

   @Contract(
      value = "!null -> !null; null -> null",
      pure = true
   )
   @Nullable
   default O deserializeOrNull(@Nullable R input) {
      return (O)this.deserializeOr(input, (Component)null);
   }

   @Contract(
      value = "!null, _ -> !null; null, _ -> param2",
      pure = true
   )
   @Nullable
   default O deserializeOr(@Nullable R input, @Nullable O fallback) {
      return (O)(input == null ? fallback : this.deserialize(input));
   }

   @NotNull
   R serialize(@NotNull I var1);

   @Contract(
      value = "!null -> !null; null -> null",
      pure = true
   )
   @Nullable
   default R serializeOrNull(@Nullable I component) {
      return (R)this.serializeOr(component, (Object)null);
   }

   @Contract(
      value = "!null, _ -> !null; null, _ -> param2",
      pure = true
   )
   @Nullable
   default R serializeOr(@Nullable I component, @Nullable R fallback) {
      return (R)(component == null ? fallback : this.serialize(component));
   }
}
