package me.lucko.spark.lib.adventure.audience;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collector;
import me.lucko.spark.lib.adventure.bossbar.BossBar;
import me.lucko.spark.lib.adventure.identity.Identified;
import me.lucko.spark.lib.adventure.identity.Identity;
import me.lucko.spark.lib.adventure.inventory.Book;
import me.lucko.spark.lib.adventure.pointer.Pointered;
import me.lucko.spark.lib.adventure.sound.Sound;
import me.lucko.spark.lib.adventure.sound.SoundStop;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.ComponentLike;
import me.lucko.spark.lib.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

public interface Audience extends Pointered {
   @NotNull
   static Audience empty() {
      return EmptyAudience.INSTANCE;
   }

   @NotNull
   static Audience audience(@NotNull Audience... audiences) {
      int length = audiences.length;
      if (length == 0) {
         return empty();
      } else {
         return (Audience)(length == 1 ? audiences[0] : audience(Arrays.asList(audiences)));
      }
   }

   @NotNull
   static ForwardingAudience audience(@NotNull Iterable<? extends Audience> audiences) {
      return () -> {
         return audiences;
      };
   }

   @NotNull
   static Collector<? super Audience, ?, ForwardingAudience> toAudience() {
      return Audiences.COLLECTOR;
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull ComponentLike message) {
      this.sendMessage(Identity.nil(), message);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull Identified source, @NotNull ComponentLike message) {
      this.sendMessage(source, message.asComponent());
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull Identity source, @NotNull ComponentLike message) {
      this.sendMessage(source, message.asComponent());
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull Component message) {
      this.sendMessage(Identity.nil(), message);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull Identified source, @NotNull Component message) {
      this.sendMessage(source, message, MessageType.SYSTEM);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull Identity source, @NotNull Component message) {
      this.sendMessage(source, message, MessageType.SYSTEM);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull ComponentLike message, @NotNull MessageType type) {
      this.sendMessage(Identity.nil(), message, type);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull Identified source, @NotNull ComponentLike message, @NotNull MessageType type) {
      this.sendMessage(source, message.asComponent(), type);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull Identity source, @NotNull ComponentLike message, @NotNull MessageType type) {
      this.sendMessage(source, message.asComponent(), type);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull Component message, @NotNull MessageType type) {
      this.sendMessage(Identity.nil(), message, type);
   }

   default void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
      this.sendMessage(source.identity(), message, type);
   }

   default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendActionBar(@NotNull ComponentLike message) {
      this.sendActionBar(message.asComponent());
   }

   default void sendActionBar(@NotNull Component message) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendPlayerListHeader(@NotNull ComponentLike header) {
      this.sendPlayerListHeader(header.asComponent());
   }

   default void sendPlayerListHeader(@NotNull Component header) {
      this.sendPlayerListHeaderAndFooter(header, Component.empty());
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendPlayerListFooter(@NotNull ComponentLike footer) {
      this.sendPlayerListFooter(footer.asComponent());
   }

   default void sendPlayerListFooter(@NotNull Component footer) {
      this.sendPlayerListHeaderAndFooter(Component.empty(), footer);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendPlayerListHeaderAndFooter(@NotNull ComponentLike header, @NotNull ComponentLike footer) {
      this.sendPlayerListHeaderAndFooter(header.asComponent(), footer.asComponent());
   }

   default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
   }

   default void showTitle(@NotNull Title title) {
   }

   default void clearTitle() {
   }

   default void resetTitle() {
   }

   default void showBossBar(@NotNull BossBar bar) {
   }

   default void hideBossBar(@NotNull BossBar bar) {
   }

   default void playSound(@NotNull Sound sound) {
   }

   default void playSound(@NotNull Sound sound, double x, double y, double z) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void stopSound(@NotNull Sound sound) {
      this.stopSound(((Sound)Objects.requireNonNull(sound, "sound")).asStop());
   }

   default void playSound(@NotNull Sound sound, @NotNull Sound.Emitter emitter) {
   }

   default void stopSound(@NotNull SoundStop stop) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void openBook(@NotNull Book.Builder book) {
      this.openBook(book.build());
   }

   default void openBook(@NotNull Book book) {
   }
}
