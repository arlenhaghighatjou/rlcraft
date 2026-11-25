package me.lucko.spark.lib.adventure.text.format;

import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class IfAbsentOnTargetMerger implements Merger {
   static final IfAbsentOnTargetMerger INSTANCE = new IfAbsentOnTargetMerger();

   private IfAbsentOnTargetMerger() {
   }

   public void mergeColor(StyleImpl.BuilderImpl target, @Nullable TextColor color) {
      if (target.color == null) {
         target.color(color);
      }

   }

   public void mergeDecoration(StyleImpl.BuilderImpl target, @NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
      target.decorationIfAbsent(decoration, state);
   }

   public void mergeClickEvent(StyleImpl.BuilderImpl target, @Nullable ClickEvent event) {
      if (target.clickEvent == null) {
         target.clickEvent(event);
      }

   }

   public void mergeHoverEvent(StyleImpl.BuilderImpl target, @Nullable HoverEvent<?> event) {
      if (target.hoverEvent == null) {
         target.hoverEvent(event);
      }

   }

   public void mergeInsertion(StyleImpl.BuilderImpl target, @Nullable String insertion) {
      if (target.insertion == null) {
         target.insertion(insertion);
      }

   }

   public void mergeFont(StyleImpl.BuilderImpl target, @Nullable Key font) {
      if (target.font == null) {
         target.font(font);
      }

   }
}
