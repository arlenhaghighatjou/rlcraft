package me.lucko.spark.lib.adventure.text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ScoreComponentImpl extends AbstractComponent implements ScoreComponent {
   private final String name;
   private final String objective;
   /** @deprecated */
   @Deprecated
   @Nullable
   private final String value;

   ScoreComponentImpl(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String name, @NotNull String objective, @Nullable String value) {
      super(children, style);
      this.name = name;
      this.objective = objective;
      this.value = value;
   }

   @NotNull
   public String name() {
      return this.name;
   }

   @NotNull
   public ScoreComponent name(@NotNull String name) {
      return Objects.equals(this.name, name) ? this : new ScoreComponentImpl(this.children, this.style, (String)Objects.requireNonNull(name, "name"), this.objective, this.value);
   }

   @NotNull
   public String objective() {
      return this.objective;
   }

   @NotNull
   public ScoreComponent objective(@NotNull String objective) {
      return Objects.equals(this.objective, objective) ? this : new ScoreComponentImpl(this.children, this.style, this.name, (String)Objects.requireNonNull(objective, "objective"), this.value);
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public String value() {
      return this.value;
   }

   /** @deprecated */
   @Deprecated
   @NotNull
   public ScoreComponent value(@Nullable String value) {
      return Objects.equals(this.value, value) ? this : new ScoreComponentImpl(this.children, this.style, this.name, this.objective, value);
   }

   @NotNull
   public ScoreComponent children(@NotNull List<? extends ComponentLike> children) {
      return new ScoreComponentImpl(children, this.style, this.name, this.objective, this.value);
   }

   @NotNull
   public ScoreComponent style(@NotNull Style style) {
      return new ScoreComponentImpl(this.children, style, this.name, this.objective, this.value);
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof ScoreComponent)) {
         return false;
      } else if (!super.equals(other)) {
         return false;
      } else {
         ScoreComponent that = (ScoreComponent)other;
         return Objects.equals(this.name, that.name()) && Objects.equals(this.objective, that.objective()) && Objects.equals(this.value, that.value());
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + this.name.hashCode();
      result = 31 * result + this.objective.hashCode();
      result = 31 * result + Objects.hashCode(this.value);
      return result;
   }

   @NotNull
   protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
      return Stream.concat(Stream.of(ExaminableProperty.of("name", this.name), ExaminableProperty.of("objective", this.objective), ExaminableProperty.of("value", this.value)), super.examinablePropertiesWithoutChildren());
   }

   @NotNull
   public ScoreComponent.Builder toBuilder() {
      return new ScoreComponentImpl.BuilderImpl(this);
   }

   static final class BuilderImpl extends AbstractComponentBuilder<ScoreComponent, ScoreComponent.Builder> implements ScoreComponent.Builder {
      @Nullable
      private String name;
      @Nullable
      private String objective;
      @Nullable
      private String value;

      BuilderImpl() {
      }

      BuilderImpl(@NotNull ScoreComponent component) {
         super(component);
         this.name = component.name();
         this.objective = component.objective();
         this.value = component.value();
      }

      @NotNull
      public ScoreComponent.Builder name(@NotNull String name) {
         this.name = name;
         return this;
      }

      @NotNull
      public ScoreComponent.Builder objective(@NotNull String objective) {
         this.objective = objective;
         return this;
      }

      /** @deprecated */
      @Deprecated
      @NotNull
      public ScoreComponent.Builder value(@Nullable String value) {
         this.value = value;
         return this;
      }

      @NotNull
      public ScoreComponent build() {
         if (this.name == null) {
            throw new IllegalStateException("name must be set");
         } else if (this.objective == null) {
            throw new IllegalStateException("objective must be set");
         } else {
            return new ScoreComponentImpl(this.children, this.buildStyle(), this.name, this.objective, this.value);
         }
      }
   }
}
