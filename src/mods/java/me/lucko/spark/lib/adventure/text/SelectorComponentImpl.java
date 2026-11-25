package me.lucko.spark.lib.adventure.text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SelectorComponentImpl extends AbstractComponent implements SelectorComponent {
   private final String pattern;
   @Nullable
   private final Component separator;

   SelectorComponentImpl(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String pattern, @Nullable ComponentLike separator) {
      super(children, style);
      this.pattern = pattern;
      this.separator = ComponentLike.unbox(separator);
   }

   @NotNull
   public String pattern() {
      return this.pattern;
   }

   @NotNull
   public SelectorComponent pattern(@NotNull String pattern) {
      return Objects.equals(this.pattern, pattern) ? this : new SelectorComponentImpl(this.children, this.style, (String)Objects.requireNonNull(pattern, "pattern"), this.separator);
   }

   @Nullable
   public Component separator() {
      return this.separator;
   }

   @NotNull
   public SelectorComponent separator(@Nullable ComponentLike separator) {
      return new SelectorComponentImpl(this.children, this.style, this.pattern, separator);
   }

   @NotNull
   public SelectorComponent children(@NotNull List<? extends ComponentLike> children) {
      return new SelectorComponentImpl(children, this.style, this.pattern, this.separator);
   }

   @NotNull
   public SelectorComponent style(@NotNull Style style) {
      return new SelectorComponentImpl(this.children, style, this.pattern, this.separator);
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof SelectorComponent)) {
         return false;
      } else if (!super.equals(other)) {
         return false;
      } else {
         SelectorComponent that = (SelectorComponent)other;
         return Objects.equals(this.pattern, that.pattern()) && Objects.equals(this.separator, that.separator());
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + this.pattern.hashCode();
      result = 31 * result + Objects.hashCode(this.separator);
      return result;
   }

   @NotNull
   protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
      return Stream.concat(Stream.of(ExaminableProperty.of("pattern", this.pattern), ExaminableProperty.of("separator", (Object)this.separator)), super.examinablePropertiesWithoutChildren());
   }

   @NotNull
   public SelectorComponent.Builder toBuilder() {
      return new SelectorComponentImpl.BuilderImpl(this);
   }

   static final class BuilderImpl extends AbstractComponentBuilder<SelectorComponent, SelectorComponent.Builder> implements SelectorComponent.Builder {
      @Nullable
      private String pattern;
      @Nullable
      private Component separator;

      BuilderImpl() {
      }

      BuilderImpl(@NotNull SelectorComponent component) {
         super(component);
         this.pattern = component.pattern();
      }

      @NotNull
      public SelectorComponent.Builder pattern(@NotNull String pattern) {
         this.pattern = pattern;
         return this;
      }

      @NotNull
      public SelectorComponent.Builder separator(@Nullable ComponentLike separator) {
         this.separator = ComponentLike.unbox(separator);
         return this;
      }

      @NotNull
      public SelectorComponent build() {
         if (this.pattern == null) {
            throw new IllegalStateException("pattern must be set");
         } else {
            return new SelectorComponentImpl(this.children, this.buildStyle(), this.pattern, this.separator);
         }
      }
   }
}
