package me.lucko.spark.lib.adventure.examination;

import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Examinable {
   @NonNull
   default String examinableName() {
      return this.getClass().getSimpleName();
   }

   @NonNull
   default Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.empty();
   }

   @NonNull
   default <R> R examine(@NonNull Examiner<R> examiner) {
      return (R)examiner.examine(this);
   }
}
