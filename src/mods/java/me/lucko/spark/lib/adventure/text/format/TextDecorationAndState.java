package me.lucko.spark.lib.adventure.text.format;

import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface TextDecorationAndState extends Examinable, StyleBuilderApplicable {
   @NonNull
   TextDecoration decoration();

   @NonNull
   TextDecoration.State state();

   default void styleApply(@NonNull Style.Builder style) {
      style.decoration(this.decoration(), this.state());
   }

   @NonNull
   default Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("decoration", (Object)this.decoration()), ExaminableProperty.of("state", (Object)this.state()));
   }
}
