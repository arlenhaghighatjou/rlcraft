package me.lucko.spark.lib.adventure.title;

import java.time.Duration;
import java.util.Objects;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.examination.string.StringExaminer;
import me.lucko.spark.lib.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TitleImpl implements Title {
   private final Component title;
   private final Component subtitle;
   @Nullable
   private final Title.Times times;

   TitleImpl(@NotNull Component title, @NotNull Component subtitle, @Nullable Title.Times times) {
      this.title = title;
      this.subtitle = subtitle;
      this.times = times;
   }

   @NotNull
   public Component title() {
      return this.title;
   }

   @NotNull
   public Component subtitle() {
      return this.subtitle;
   }

   @Nullable
   public Title.Times times() {
      return this.times;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         TitleImpl that = (TitleImpl)other;
         return this.title.equals(that.title) && this.subtitle.equals(that.subtitle) && Objects.equals(this.times, that.times);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.title.hashCode();
      result = 31 * result + this.subtitle.hashCode();
      result = 31 * result + Objects.hashCode(this.times);
      return result;
   }

   @NotNull
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("title", (Object)this.title), ExaminableProperty.of("subtitle", (Object)this.subtitle), ExaminableProperty.of("times", (Object)this.times));
   }

   public String toString() {
      return (String)this.examine(StringExaminer.simpleEscaping());
   }

   static class TimesImpl implements Title.Times {
      private final Duration fadeIn;
      private final Duration stay;
      private final Duration fadeOut;

      TimesImpl(Duration fadeIn, Duration stay, Duration fadeOut) {
         this.fadeIn = fadeIn;
         this.stay = stay;
         this.fadeOut = fadeOut;
      }

      @NotNull
      public Duration fadeIn() {
         return this.fadeIn;
      }

      @NotNull
      public Duration stay() {
         return this.stay;
      }

      @NotNull
      public Duration fadeOut() {
         return this.fadeOut;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (other != null && this.getClass() == other.getClass()) {
            TitleImpl.TimesImpl that = (TitleImpl.TimesImpl)other;
            return this.fadeIn.equals(that.fadeIn) && this.stay.equals(that.stay) && this.fadeOut.equals(that.fadeOut);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.fadeIn.hashCode();
         result = 31 * result + this.stay.hashCode();
         result = 31 * result + this.fadeOut.hashCode();
         return result;
      }

      @NotNull
      public Stream<? extends ExaminableProperty> examinableProperties() {
         return Stream.of(ExaminableProperty.of("fadeIn", (Object)this.fadeIn), ExaminableProperty.of("stay", (Object)this.stay), ExaminableProperty.of("fadeOut", (Object)this.fadeOut));
      }

      public String toString() {
         return (String)this.examine(StringExaminer.simpleEscaping());
      }
   }
}
