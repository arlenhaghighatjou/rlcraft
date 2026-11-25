package me.lucko.spark.lib.adventure.text.format;

import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class AlwaysMerger implements Merger {
   static final AlwaysMerger INSTANCE = new AlwaysMerger();

   private AlwaysMerger() {
   }

   public void mergeColor(StyleImpl.BuilderImpl target, @Nullable TextColor color) {
      target.color(color);
   }

   public void mergeDecoration(StyleImpl.BuilderImpl target, @NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
      target.decoration(decoration, state);
   }

   public void mergeClickEvent(StyleImpl.BuilderImpl target, @Nullable ClickEvent event) {
      target.clickEvent(event);
   }

   public void mergeHoverEvent(StyleImpl.BuilderImpl target, @Nullable HoverEvent<?> event) {
      target.hoverEvent(event);
   }

   public void mergeInsertion(StyleImpl.BuilderImpl target, @Nullable String insertion) {
      target.insertion(insertion);
   }

   public void mergeFont(StyleImpl.BuilderImpl target, @Nullable Key font) {
      target.font(font);
   }
}
