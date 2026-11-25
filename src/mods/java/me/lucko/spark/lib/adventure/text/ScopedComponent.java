package me.lucko.spark.lib.adventure.text;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEventSource;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.text.format.TextColor;
import me.lucko.spark.lib.adventure.text.format.TextDecoration;
import me.lucko.spark.lib.adventure.util.MonkeyBars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ScopedComponent<C extends Component> extends Component {
   @NotNull
   C children(@NotNull List<? extends ComponentLike> var1);

   @NotNull
   C style(@NotNull Style var1);

   @NotNull
   default C style(@NotNull Consumer<Style.Builder> style) {
      return (C)super.style(style);
   }

   @NotNull
   default C style(@NotNull Style.Builder style) {
      return (C)super.style(style);
   }

   @NotNull
   default C mergeStyle(@NotNull Component that) {
      return (C)super.mergeStyle(that);
   }

   @NotNull
   default C mergeStyle(@NotNull Component that, @NotNull Style.Merge... merges) {
      return (C)super.mergeStyle(that, merges);
   }

   @NotNull
   default C append(@NotNull Component component) {
      if (component == Component.empty()) {
         return (C)this;
      } else {
         List<Component> oldChildren = this.children();
         return (C)this.children(MonkeyBars.addOne(oldChildren, component));
      }
   }

   @NotNull
   default C append(@NotNull ComponentLike component) {
      return (C)super.append(component);
   }

   @NotNull
   default C append(@NotNull ComponentBuilder<?, ?> builder) {
      return (C)super.append(builder);
   }

   @NotNull
   default C mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
      return (C)super.mergeStyle(that, merges);
   }

   @NotNull
   default C color(@Nullable TextColor color) {
      return (C)super.color(color);
   }

   @NotNull
   default C colorIfAbsent(@Nullable TextColor color) {
      return (C)super.colorIfAbsent(color);
   }

   @NotNull
   default Component decorate(@NotNull TextDecoration decoration) {
      return super.decorate(decoration);
   }

   @NotNull
   default C decoration(@NotNull TextDecoration decoration, boolean flag) {
      return (C)super.decoration(decoration, flag);
   }

   @NotNull
   default C decoration(@NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
      return (C)super.decoration(decoration, state);
   }

   @NotNull
   default C clickEvent(@Nullable ClickEvent event) {
      return (C)super.clickEvent(event);
   }

   @NotNull
   default C hoverEvent(@Nullable HoverEventSource<?> event) {
      return (C)super.hoverEvent(event);
   }

   @NotNull
   default C insertion(@Nullable String insertion) {
      return (C)super.insertion(insertion);
   }
}
