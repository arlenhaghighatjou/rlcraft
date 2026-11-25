package me.lucko.spark.lib.adventure.pointer;

import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public interface Pointer<V> extends Examinable {
   @NotNull
   static <V> Pointer<V> pointer(@NotNull Class<V> type, @NotNull Key key) {
      return new PointerImpl<V>(type, key);
   }

   @NotNull
   Class<V> type();

   @NotNull
   Key key();

   @NotNull
   default Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("type", (Object)this.type()), ExaminableProperty.of("key", (Object)this.key()));
   }
}
