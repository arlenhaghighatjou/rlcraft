package me.lucko.spark.lib.adventure.key;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public interface KeyedValue<T> extends Keyed {
   @NotNull
   static <T> KeyedValue<T> of(@NotNull Key key, @NotNull T value) {
      return new KeyedValueImpl<T>(key, Objects.requireNonNull(value, "value"));
   }

   @NotNull
   T value();
}
