package me.lucko.spark.lib.adventure.audience;

import java.util.Optional;
import java.util.function.Supplier;
import me.lucko.spark.lib.adventure.identity.Identified;
import me.lucko.spark.lib.adventure.identity.Identity;
import me.lucko.spark.lib.adventure.inventory.Book;
import me.lucko.spark.lib.adventure.pointer.Pointer;
import me.lucko.spark.lib.adventure.text.ComponentLike;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

final class EmptyAudience implements Audience {
   static final EmptyAudience INSTANCE = new EmptyAudience();

   EmptyAudience() {
   }

   @NotNull
   public <T> Optional<T> get(@NotNull Pointer<T> pointer) {
      return Optional.empty();
   }

   @Contract("_, null -> null; _, !null -> !null")
   @Nullable
   public <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
      return defaultValue;
   }

   @UnknownNullability
   public <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
      return (T)defaultValue.get();
   }

   public void sendMessage(@NotNull ComponentLike message) {
   }

   public void sendMessage(@NotNull Identified source, @NotNull ComponentLike message) {
   }

   public void sendMessage(@NotNull Identity source, @NotNull ComponentLike message) {
   }

   public void sendMessage(@NotNull ComponentLike message, @NotNull MessageType type) {
   }

   public void sendMessage(@NotNull Identified source, @NotNull ComponentLike message, @NotNull MessageType type) {
   }

   public void sendMessage(@NotNull Identity source, @NotNull ComponentLike message, @NotNull MessageType type) {
   }

   public void sendActionBar(@NotNull ComponentLike message) {
   }

   public void sendPlayerListHeader(@NotNull ComponentLike header) {
   }

   public void sendPlayerListFooter(@NotNull ComponentLike footer) {
   }

   public void sendPlayerListHeaderAndFooter(@NotNull ComponentLike header, @NotNull ComponentLike footer) {
   }

   public void openBook(@NotNull Book.Builder book) {
   }

   public boolean equals(Object that) {
      return this == that;
   }

   public int hashCode() {
      return 0;
   }

   public String toString() {
      return "EmptyAudience";
   }
}
