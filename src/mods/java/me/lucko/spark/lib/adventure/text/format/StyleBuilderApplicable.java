package me.lucko.spark.lib.adventure.text.format;

import java.util.function.Consumer;
import me.lucko.spark.lib.adventure.text.ComponentBuilder;
import me.lucko.spark.lib.adventure.text.ComponentBuilderApplicable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StyleBuilderApplicable extends ComponentBuilderApplicable {
   @Contract(
      mutates = "param"
   )
   void styleApply(@NotNull Style.Builder var1);

   default void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
      component.style(this::styleApply);
   }
}
