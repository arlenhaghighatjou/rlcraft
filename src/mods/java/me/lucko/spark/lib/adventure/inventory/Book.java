package me.lucko.spark.lib.adventure.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.util.Buildable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface Book extends Buildable<Book, Book.Builder>, Examinable {
   @NotNull
   static Book book(@NotNull Component title, @NotNull Component author, @NotNull Collection<Component> pages) {
      return new BookImpl(title, author, new ArrayList(pages));
   }

   @NotNull
   static Book book(@NotNull Component title, @NotNull Component author, @NotNull Component... pages) {
      return book(title, author, Arrays.asList(pages));
   }

   @NotNull
   static Book.Builder builder() {
      return new BookImpl.BuilderImpl();
   }

   @NotNull
   Component title();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull
   Book title(@NotNull Component var1);

   @NotNull
   Component author();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull
   Book author(@NotNull Component var1);

   @NotNull
   @Unmodifiable
   List<Component> pages();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull
   default Book pages(@NotNull Component... pages) {
      return this.pages(Arrays.asList(pages));
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull
   Book pages(@NotNull List<Component> var1);

   @NotNull
   default Book.Builder toBuilder() {
      return builder().title(this.title()).author(this.author()).pages(this.pages());
   }

   public interface Builder extends Buildable.Builder<Book> {
      @Contract("_ -> this")
      @NotNull
      Book.Builder title(@NotNull Component var1);

      @Contract("_ -> this")
      @NotNull
      Book.Builder author(@NotNull Component var1);

      @Contract("_ -> this")
      @NotNull
      Book.Builder addPage(@NotNull Component var1);

      @Contract("_ -> this")
      @NotNull
      Book.Builder pages(@NotNull Component... var1);

      @Contract("_ -> this")
      @NotNull
      Book.Builder pages(@NotNull Collection<Component> var1);

      @NotNull
      Book build();
   }
}
