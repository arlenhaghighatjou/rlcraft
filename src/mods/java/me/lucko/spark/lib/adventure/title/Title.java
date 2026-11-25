package me.lucko.spark.lib.adventure.title;

import java.time.Duration;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface Title extends Examinable {
   Title.Times DEFAULT_TIMES = Title.Times.of(Ticks.duration(10L), Ticks.duration(70L), Ticks.duration(20L));

   @NotNull
   static Title title(@NotNull Component title, @NotNull Component subtitle) {
      return title(title, subtitle, DEFAULT_TIMES);
   }

   @NotNull
   static Title title(@NotNull Component title, @NotNull Component subtitle, @Nullable Title.Times times) {
      return new TitleImpl(title, subtitle, times);
   }

   @NotNull
   Component title();

   @NotNull
   Component subtitle();

   @Nullable
   Title.Times times();

   public interface Times extends Examinable {
      @NotNull
      static Title.Times of(@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut) {
         return new TitleImpl.TimesImpl(fadeIn, stay, fadeOut);
      }

      @NotNull
      Duration fadeIn();

      @NotNull
      Duration stay();

      @NotNull
      Duration fadeOut();
   }
}
