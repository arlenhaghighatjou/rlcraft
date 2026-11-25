package me.lucko.spark.lib.adventure.text.format;

import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

interface Merger {
   void mergeColor(StyleImpl.BuilderImpl var1, @Nullable TextColor var2);

   void mergeDecoration(StyleImpl.BuilderImpl var1, @NotNull TextDecoration var2, @NotNull TextDecoration.State var3);

   void mergeClickEvent(StyleImpl.BuilderImpl var1, @Nullable ClickEvent var2);

   void mergeHoverEvent(StyleImpl.BuilderImpl var1, @Nullable HoverEvent<?> var2);

   void mergeInsertion(StyleImpl.BuilderImpl var1, @Nullable String var2);

   void mergeFont(StyleImpl.BuilderImpl var1, @Nullable Key var2);
}
