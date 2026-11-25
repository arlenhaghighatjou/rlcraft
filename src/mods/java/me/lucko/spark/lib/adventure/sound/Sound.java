package me.lucko.spark.lib.adventure.sound;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.key.Keyed;
import me.lucko.spark.lib.adventure.util.Index;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface Sound extends Examinable {
   @NotNull
   static Sound sound(@NotNull final Key name, @NotNull Sound.Source source, float volume, float pitch) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(source, "source");
      return new SoundImpl(source, volume, pitch) {
         @NotNull
         public Key name() {
            return name;
         }
      };
   }

   @NotNull
   static Sound sound(@NotNull Sound.Type type, @NotNull Sound.Source source, float volume, float pitch) {
      Objects.requireNonNull(type, "type");
      return sound(type.key(), source, volume, pitch);
   }

   @NotNull
   static Sound sound(@NotNull final Supplier<? extends Sound.Type> type, @NotNull Sound.Source source, float volume, float pitch) {
      Objects.requireNonNull(type, "type");
      Objects.requireNonNull(source, "source");
      return new SoundImpl(source, volume, pitch) {
         @NotNull
         public Key name() {
            return ((Sound.Type)type.get()).key();
         }
      };
   }

   @NotNull
   static Sound sound(@NotNull Key name, @NotNull Sound.Source.Provider source, float volume, float pitch) {
      return sound(name, source.soundSource(), volume, pitch);
   }

   @NotNull
   static Sound sound(@NotNull Sound.Type type, @NotNull Sound.Source.Provider source, float volume, float pitch) {
      return sound(type, source.soundSource(), volume, pitch);
   }

   @NotNull
   static Sound sound(@NotNull Supplier<? extends Sound.Type> type, @NotNull Sound.Source.Provider source, float volume, float pitch) {
      return sound(type, source.soundSource(), volume, pitch);
   }

   @NotNull
   Key name();

   @NotNull
   Sound.Source source();

   float volume();

   float pitch();

   @NotNull
   SoundStop asStop();

   public interface Emitter {
      @NotNull
      static Sound.Emitter self() {
         return SoundImpl.EMITTER_SELF;
      }
   }

   public static enum Source {
      MASTER("master"),
      MUSIC("music"),
      RECORD("record"),
      WEATHER("weather"),
      BLOCK("block"),
      HOSTILE("hostile"),
      NEUTRAL("neutral"),
      PLAYER("player"),
      AMBIENT("ambient"),
      VOICE("voice");

      public static final Index<String, Sound.Source> NAMES = Index.create(Sound.Source.class, (source) -> {
         return source.name;
      });
      private final String name;

      private Source(String name) {
         this.name = name;
      }

      public interface Provider {
         @NotNull
         Sound.Source soundSource();
      }
   }

   public interface Type extends Keyed {
      @NotNull
      Key key();
   }
}
