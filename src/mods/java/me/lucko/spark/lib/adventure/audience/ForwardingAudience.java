package me.lucko.spark.lib.adventure.audience;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import me.lucko.spark.lib.adventure.bossbar.BossBar;
import me.lucko.spark.lib.adventure.identity.Identified;
import me.lucko.spark.lib.adventure.identity.Identity;
import me.lucko.spark.lib.adventure.inventory.Book;
import me.lucko.spark.lib.adventure.pointer.Pointer;
import me.lucko.spark.lib.adventure.sound.Sound;
import me.lucko.spark.lib.adventure.sound.SoundStop;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.title.Title;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

@FunctionalInterface
public interface ForwardingAudience extends Audience {
   @OverrideOnly
   @NotNull
   Iterable<? extends Audience> audiences();

   @NotNull
   default <T> Optional<T> get(@NotNull Pointer<T> pointer) {
      return Optional.empty();
   }

   @Contract("_, null -> null; _, !null -> !null")
   @Nullable
   default <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
      return defaultValue;
   }

   @UnknownNullability
   default <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
      return (T)defaultValue.get();
   }

   default void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
      for(Audience audience : this.audiences()) {
         audience.sendMessage(source, message, type);
      }

   }

   default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
      for(Audience audience : this.audiences()) {
         audience.sendMessage(source, message, type);
      }

   }

   default void sendActionBar(@NotNull Component message) {
      for(Audience audience : this.audiences()) {
         audience.sendActionBar(message);
      }

   }

   default void sendPlayerListHeader(@NotNull Component header) {
      for(Audience audience : this.audiences()) {
         audience.sendPlayerListHeader(header);
      }

   }

   default void sendPlayerListFooter(@NotNull Component footer) {
      for(Audience audience : this.audiences()) {
         audience.sendPlayerListFooter(footer);
      }

   }

   default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
      for(Audience audience : this.audiences()) {
         audience.sendPlayerListHeaderAndFooter(header, footer);
      }

   }

   default void showTitle(@NotNull Title title) {
      for(Audience audience : this.audiences()) {
         audience.showTitle(title);
      }

   }

   default void clearTitle() {
      for(Audience audience : this.audiences()) {
         audience.clearTitle();
      }

   }

   default void resetTitle() {
      for(Audience audience : this.audiences()) {
         audience.resetTitle();
      }

   }

   default void showBossBar(@NotNull BossBar bar) {
      for(Audience audience : this.audiences()) {
         audience.showBossBar(bar);
      }

   }

   default void hideBossBar(@NotNull BossBar bar) {
      for(Audience audience : this.audiences()) {
         audience.hideBossBar(bar);
      }

   }

   default void playSound(@NotNull Sound sound) {
      for(Audience audience : this.audiences()) {
         audience.playSound(sound);
      }

   }

   default void playSound(@NotNull Sound sound, double x, double y, double z) {
      for(Audience audience : this.audiences()) {
         audience.playSound(sound, x, y, z);
      }

   }

   default void playSound(@NotNull Sound sound, @NotNull Sound.Emitter emitter) {
      for(Audience audience : this.audiences()) {
         audience.playSound(sound, emitter);
      }

   }

   default void stopSound(@NotNull SoundStop stop) {
      for(Audience audience : this.audiences()) {
         audience.stopSound(stop);
      }

   }

   default void openBook(@NotNull Book book) {
      for(Audience audience : this.audiences()) {
         audience.openBook(book);
      }

   }

   public interface Single extends ForwardingAudience {
      @OverrideOnly
      @NotNull
      Audience audience();

      /** @deprecated */
      @Deprecated
      @NotNull
      default Iterable<? extends Audience> audiences() {
         return Collections.singleton(this.audience());
      }

      @NotNull
      default <T> Optional<T> get(@NotNull Pointer<T> pointer) {
         return this.audience().get(pointer);
      }

      @Contract("_, null -> null; _, !null -> !null")
      @Nullable
      default <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
         return (T)this.audience().getOrDefault(pointer, defaultValue);
      }

      @UnknownNullability
      default <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
         return (T)this.audience().getOrDefaultFrom(pointer, defaultValue);
      }

      default void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
         this.audience().sendMessage(source, message, type);
      }

      default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
         this.audience().sendMessage(source, message, type);
      }

      default void sendActionBar(@NotNull Component message) {
         this.audience().sendActionBar(message);
      }

      default void sendPlayerListHeader(@NotNull Component header) {
         this.audience().sendPlayerListHeader(header);
      }

      default void sendPlayerListFooter(@NotNull Component footer) {
         this.audience().sendPlayerListFooter(footer);
      }

      default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
         this.audience().sendPlayerListHeaderAndFooter(header, footer);
      }

      default void showTitle(@NotNull Title title) {
         this.audience().showTitle(title);
      }

      default void clearTitle() {
         this.audience().clearTitle();
      }

      default void resetTitle() {
         this.audience().resetTitle();
      }

      default void showBossBar(@NotNull BossBar bar) {
         this.audience().showBossBar(bar);
      }

      default void hideBossBar(@NotNull BossBar bar) {
         this.audience().hideBossBar(bar);
      }

      default void playSound(@NotNull Sound sound) {
         this.audience().playSound(sound);
      }

      default void playSound(@NotNull Sound sound, double x, double y, double z) {
         this.audience().playSound(sound, x, y, z);
      }

      default void playSound(@NotNull Sound sound, @NotNull Sound.Emitter emitter) {
         this.audience().playSound(sound, emitter);
      }

      default void stopSound(@NotNull SoundStop stop) {
         this.audience().stopSound(stop);
      }

      default void openBook(@NotNull Book book) {
         this.audience().openBook(book);
      }
   }
}
