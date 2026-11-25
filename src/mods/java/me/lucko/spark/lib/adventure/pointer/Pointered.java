package me.lucko.spark.lib.adventure.pointer;

import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public interface Pointered {
   @NotNull
   default <T> Optional<T> get(@NotNull Pointer<T> pointer) {
      return this.pointers().<T>get(pointer);
   }

   @Contract("_, null -> null; _, !null -> !null")
   @Nullable
   default <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
      return (T)this.pointers().getOrDefault(pointer, defaultValue);
   }

   @UnknownNullability
   default <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
      return (T)this.pointers().getOrDefaultFrom(pointer, defaultValue);
   }

   @NotNull
   default Pointers pointers() {
      return Pointers.empty();
   }
}
