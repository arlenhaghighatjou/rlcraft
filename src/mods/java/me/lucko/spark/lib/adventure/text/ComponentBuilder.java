package me.lucko.spark.lib.adventure.text;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEventSource;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.text.format.TextColor;
import me.lucko.spark.lib.adventure.text.format.TextDecoration;
import me.lucko.spark.lib.adventure.util.Buildable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> extends Buildable.Builder<C>, ComponentBuilderApplicable, ComponentLike {
   @Contract("_ -> this")
   @NotNull
   B append(@NotNull Component var1);

   @Contract("_ -> this")
   @NotNull
   default B append(@NotNull ComponentLike component) {
      return (B)this.append(component.asComponent());
   }

   @Contract("_ -> this")
   @NotNull
   default B append(@NotNull ComponentBuilder<?, ?> builder) {
      return (B)this.append((Component)builder.build());
   }

   @Contract("_ -> this")
   @NotNull
   B append(@NotNull Component... var1);

   @Contract("_ -> this")
   @NotNull
   B append(@NotNull ComponentLike... var1);

   @Contract("_ -> this")
   @NotNull
   B append(@NotNull Iterable<? extends ComponentLike> var1);

   @Contract("_ -> this")
   @NotNull
   default B apply(@NotNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
      consumer.accept(this);
      return (B)this;
   }

   @Contract("_ -> this")
   @NotNull
   B applyDeep(@NotNull Consumer<? super ComponentBuilder<?, ?>> var1);

   @Contract("_ -> this")
   @NotNull
   B mapChildren(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> var1);

   @Contract("_ -> this")
   @NotNull
   B mapChildrenDeep(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> var1);

   @NotNull
   List<Component> children();

   @Contract("_ -> this")
   @NotNull
   B style(@NotNull Style var1);

   @Contract("_ -> this")
   @NotNull
   B style(@NotNull Consumer<Style.Builder> var1);

   @Contract("_ -> this")
   @NotNull
   B font(@Nullable Key var1);

   @Contract("_ -> this")
   @NotNull
   B color(@Nullable TextColor var1);

   @Contract("_ -> this")
   @NotNull
   B colorIfAbsent(@Nullable TextColor var1);

   @Contract("_, _ -> this")
   @NotNull
   default B decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
      TextDecoration.State state = TextDecoration.State.byBoolean(flag);
      decorations.forEach((decoration) -> {
         this.decoration(decoration, state);
      });
      return (B)this;
   }

   @Contract("_ -> this")
   @NotNull
   default B decorate(@NotNull TextDecoration decoration) {
      return (B)this.decoration(decoration, TextDecoration.State.TRUE);
   }

   @Contract("_ -> this")
   @NotNull
   default B decorate(@NotNull TextDecoration... decorations) {
      int i = 0;

      for(int length = decorations.length; i < length; ++i) {
         this.decorate(decorations[i]);
      }

      return (B)this;
   }

   @Contract("_, _ -> this")
   @NotNull
   default B decoration(@NotNull TextDecoration decoration, boolean flag) {
      return (B)this.decoration(decoration, TextDecoration.State.byBoolean(flag));
   }

   @Contract("_, _ -> this")
   @NotNull
   B decoration(@NotNull TextDecoration var1, @NotNull TextDecoration.State var2);

   @Contract("_ -> this")
   @NotNull
   B clickEvent(@Nullable ClickEvent var1);

   @Contract("_ -> this")
   @NotNull
   B hoverEvent(@Nullable HoverEventSource<?> var1);

   @Contract("_ -> this")
   @NotNull
   B insertion(@Nullable String var1);

   @Contract("_ -> this")
   @NotNull
   default B mergeStyle(@NotNull Component that) {
      return (B)this.mergeStyle(that, Style.Merge.all());
   }

   @Contract("_, _ -> this")
   @NotNull
   default B mergeStyle(@NotNull Component that, @NotNull Style.Merge... merges) {
      return (B)this.mergeStyle(that, Style.Merge.of(merges));
   }

   @Contract("_, _ -> this")
   @NotNull
   B mergeStyle(@NotNull Component var1, @NotNull Set<Style.Merge> var2);

   @NotNull
   B resetStyle();

   @NotNull
   C build();

   @Contract("_ -> this")
   @NotNull
   default B applicableApply(@NotNull ComponentBuilderApplicable applicable) {
      applicable.componentBuilderApply(this);
      return (B)this;
   }

   default void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
      component.append(this);
   }

   @NotNull
   default Component asComponent() {
      return this.build();
   }
}
