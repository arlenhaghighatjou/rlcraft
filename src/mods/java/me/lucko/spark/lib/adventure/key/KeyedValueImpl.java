package me.lucko.spark.lib.adventure.key;

import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class KeyedValueImpl<T> implements Examinable, KeyedValue<T> {
   private final Key key;
   private final T value;

   KeyedValueImpl(Key key, T value) {
      this.key = key;
      this.value = value;
   }

   @NotNull
   public Key key() {
      return this.key;
   }

   @NotNull
   public T value() {
      return this.value;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         KeyedValueImpl<?> that = (KeyedValueImpl)other;
         return this.key.equals(that.key) && this.value.equals(that.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.key.hashCode();
      result = 31 * result + this.value.hashCode();
      return result;
   }

   @NotNull
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("key", (Object)this.key), ExaminableProperty.of("value", this.value));
   }

   public String toString() {
      return (String)this.examine(StringExaminer.simpleEscaping());
   }
}
