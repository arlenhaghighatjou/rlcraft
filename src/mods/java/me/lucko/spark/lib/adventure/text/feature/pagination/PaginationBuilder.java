package me.lucko.spark.lib.adventure.text.feature.pagination;

import java.util.function.Consumer;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

final class PaginationBuilder implements Pagination.Builder {
   private int width = 55;
   private int resultsPerPage = 6;
   private char lineCharacter = '-';
   private Style lineStyle = Pagination.LINE_STYLE;
   private Pagination.Renderer renderer = Pagination.DEFAULT_RENDERER;
   private char previousPageButtonCharacter = 171;
   private Style previousPageButtonStyle = Pagination.PREVIOUS_PAGE_BUTTON_STYLE;
   private char nextPageButtonCharacter = 187;
   private Style nextPageButtonStyle = Pagination.NEXT_PAGE_BUTTON_STYLE;

   PaginationBuilder() {
   }

   @NotNull
   public Pagination.Builder width(int width) {
      this.width = width;
      return this;
   }

   @NotNull
   public Pagination.Builder resultsPerPage(@Range(from = 0L,to = 2147483647L) int resultsPerPage) {
      this.resultsPerPage = resultsPerPage;
      return this;
   }

   @NotNull
   public Pagination.Builder renderer(@NotNull Pagination.Renderer renderer) {
      this.renderer = renderer;
      return this;
   }

   @NotNull
   public Pagination.Builder line(@NotNull Consumer<Pagination.Builder.CharacterAndStyle> line) {
      line.accept(new Pagination.Builder.CharacterAndStyle() {
         @NotNull
         public Pagination.Builder.CharacterAndStyle character(char character) {
            PaginationBuilder.this.lineCharacter = character;
            return this;
         }

         @NotNull
         public Pagination.Builder.CharacterAndStyle style(@NotNull Style style) {
            PaginationBuilder.this.lineStyle = style;
            return this;
         }
      });
      return this;
   }

   @NotNull
   public Pagination.Builder previousButton(@NotNull Consumer<Pagination.Builder.CharacterAndStyle> previousButton) {
      previousButton.accept(new Pagination.Builder.CharacterAndStyle() {
         @NotNull
         public Pagination.Builder.CharacterAndStyle character(char character) {
            PaginationBuilder.this.previousPageButtonCharacter = character;
            return this;
         }

         @NotNull
         public Pagination.Builder.CharacterAndStyle style(@NotNull Style style) {
            PaginationBuilder.this.previousPageButtonStyle = style;
            return this;
         }
      });
      return this;
   }

   @NotNull
   public Pagination.Builder nextButton(@NotNull Consumer<Pagination.Builder.CharacterAndStyle> nextButton) {
      nextButton.accept(new Pagination.Builder.CharacterAndStyle() {
         @NotNull
         public Pagination.Builder.CharacterAndStyle character(char character) {
            PaginationBuilder.this.nextPageButtonCharacter = character;
            return this;
         }

         @NotNull
         public Pagination.Builder.CharacterAndStyle style(@NotNull Style style) {
            PaginationBuilder.this.nextPageButtonStyle = style;
            return this;
         }
      });
      return this;
   }

   @NotNull
   public <T> Pagination<T> build(@NotNull Component title, @NotNull Pagination.Renderer.RowRenderer<T> rowRenderer, @NotNull Pagination.PageCommandFunction pageCommand) {
      return new PaginationImpl<T>(this.width, this.resultsPerPage, this.renderer, this.lineCharacter, this.lineStyle, this.previousPageButtonCharacter, this.previousPageButtonStyle, this.nextPageButtonCharacter, this.nextPageButtonStyle, title, rowRenderer, pageCommand);
   }
}
