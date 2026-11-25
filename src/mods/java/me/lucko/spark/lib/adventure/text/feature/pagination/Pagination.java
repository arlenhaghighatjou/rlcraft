package me.lucko.spark.lib.adventure.text.feature.pagination;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import me.lucko.spark.lib.adventure.text.format.NamedTextColor;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@FunctionalInterface
public interface Pagination<T> {
   int WIDTH = 55;
   int RESULTS_PER_PAGE = 6;
   char LINE_CHARACTER = '-';
   Style LINE_STYLE = Style.style((TextColor)NamedTextColor.DARK_GRAY);
   char PREVIOUS_PAGE_BUTTON_CHARACTER = '«';
   Style PREVIOUS_PAGE_BUTTON_STYLE = Style.style(NamedTextColor.RED, HoverEvent.showText((Component)Component.text("Previous Page", NamedTextColor.RED)));
   char NEXT_PAGE_BUTTON_CHARACTER = '»';
   Style NEXT_PAGE_BUTTON_STYLE = Style.style(NamedTextColor.GREEN, HoverEvent.showText((Component)Component.text("Next Page", NamedTextColor.GREEN)));
   Pagination.Renderer DEFAULT_RENDERER = new Pagination.Renderer() {
      public String toString() {
         return "Pagination.DEFAULT_RENDERER";
      }
   };

   @NotNull
   static Pagination.Builder builder() {
      return new PaginationBuilder();
   }

   @NotNull
   List<Component> render(@NotNull Collection<? extends T> var1, int var2);

   public interface Builder {
      @NotNull
      Pagination.Builder width(int var1);

      @NotNull
      Pagination.Builder resultsPerPage(@Range(from = 0L,to = 2147483647L) int var1);

      @NotNull
      Pagination.Builder renderer(@NotNull Pagination.Renderer var1);

      @NotNull
      Pagination.Builder line(@NotNull Consumer<Pagination.Builder.CharacterAndStyle> var1);

      @NotNull
      Pagination.Builder previousButton(@NotNull Consumer<Pagination.Builder.CharacterAndStyle> var1);

      @NotNull
      Pagination.Builder nextButton(@NotNull Consumer<Pagination.Builder.CharacterAndStyle> var1);

      @NotNull
      <T> Pagination<T> build(@NotNull Component var1, @NotNull Pagination.Renderer.RowRenderer<T> var2, @NotNull Pagination.PageCommandFunction var3);

      public interface CharacterAndStyle {
         @NotNull
         Pagination.Builder.CharacterAndStyle character(char var1);

         @NotNull
         Pagination.Builder.CharacterAndStyle style(@NotNull Style var1);
      }
   }

   @FunctionalInterface
   public interface PageCommandFunction {
      @NotNull
      String pageCommand(int var1);
   }

   public interface Renderer {
      Component GRAY_LEFT_ROUND_BRACKET = Component.text("(", NamedTextColor.GRAY);
      Component GRAY_LEFT_SQUARE_BRACKET = Component.text("[", NamedTextColor.GRAY);
      Component GRAY_RIGHT_ROUND_BRACKET = Component.text(")", NamedTextColor.GRAY);
      Component GRAY_RIGHT_SQUARE_BRACKET = Component.text("]", NamedTextColor.GRAY);
      Component GRAY_FORWARD_SLASH = Component.text("/", NamedTextColor.GRAY);

      @NotNull
      default Component renderEmpty() {
         return Component.text("No results match.", NamedTextColor.GRAY);
      }

      @NotNull
      default Component renderUnknownPage(int page, int pages) {
         return Component.text("Unknown page selected. " + pages + " total pages.", NamedTextColor.GRAY);
      }

      @NotNull
      default Component renderHeader(@NotNull Component title, int page, int pages) {
         return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.space())).append(title)).append(Component.space())).append(GRAY_LEFT_ROUND_BRACKET)).append(Component.text(page, NamedTextColor.WHITE))).append(GRAY_FORWARD_SLASH)).append(Component.text(pages, NamedTextColor.WHITE))).append(GRAY_RIGHT_ROUND_BRACKET)).append(Component.space())).build();
      }

      @NotNull
      default Component renderPreviousPageButton(char character, @NotNull Style style, @NotNull ClickEvent clickEvent) {
         return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.space())).append(GRAY_LEFT_SQUARE_BRACKET)).append(Component.text(character, style.clickEvent(clickEvent)))).append(GRAY_RIGHT_SQUARE_BRACKET)).append(Component.space())).build();
      }

      @NotNull
      default Component renderNextPageButton(char character, @NotNull Style style, @NotNull ClickEvent clickEvent) {
         return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.space())).append(GRAY_LEFT_SQUARE_BRACKET)).append(Component.text(character, style.clickEvent(clickEvent)))).append(GRAY_RIGHT_SQUARE_BRACKET)).append(Component.space())).build();
      }

      @FunctionalInterface
      public interface RowRenderer<T> {
         @NotNull
         Collection<Component> renderRow(@Nullable T var1, int var2);
      }
   }
}
