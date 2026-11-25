package me.lucko.spark.lib.adventure.sound;

import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.examination.string.StringExaminer;
import me.lucko.spark.lib.adventure.util.ShadyPines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class SoundImpl implements Sound {
   static final Sound.Emitter EMITTER_SELF = new Sound.Emitter() {
      public String toString() {
         return "SelfSoundEmitter";
      }
   };
   private final Sound.Source source;
   private final float volume;
   private final float pitch;
   private SoundStop stop;

   SoundImpl(@NotNull Sound.Source source, float volume, float pitch) {
      this.source = source;
      this.volume = volume;
      this.pitch = pitch;
   }

   @NotNull
   public Sound.Source source() {
      return this.source;
   }

   public float volume() {
      return this.volume;
   }

   public float pitch() {
      return this.pitch;
   }

   @NotNull
   public SoundStop asStop() {
      if (this.stop == null) {
         this.stop = SoundStop.namedOnSource(this.name(), this.source());
      }

      return this.stop;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof SoundImpl)) {
         return false;
      } else {
         SoundImpl that = (SoundImpl)other;
         return this.name().equals(that.name()) && this.source == that.source && ShadyPines.equals(this.volume, that.volume) && ShadyPines.equals(this.pitch, that.pitch);
      }
   }

   public int hashCode() {
      int result = this.name().hashCode();
      result = 31 * result + this.source.hashCode();
      result = 31 * result + Float.hashCode(this.volume);
      result = 31 * result + Float.hashCode(this.pitch);
      return result;
   }

   @NotNull
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("name", (Object)this.name()), ExaminableProperty.of("source", (Object)this.source), ExaminableProperty.of("volume", this.volume), ExaminableProperty.of("pitch", this.pitch));
   }

   public String toString() {
      return (String)this.examine(StringExaminer.simpleEscaping());
   }
}
