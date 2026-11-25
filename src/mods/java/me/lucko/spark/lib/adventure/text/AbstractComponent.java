package me.lucko.spark.lib.adventure.text;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.examination.string.StringExaminer;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.util.Buildable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Debug.Renderer;

@Renderer(
   text = "this.debuggerString()",
   childrenArray = "this.children().toArray()",
   hasChildren = "!this.children().isEmpty()"
)
public abstract class AbstractComponent implements Component {
   private static final Predicate<Component> NOT_EMPTY = (component) -> {
      return component != Component.empty();
   };
   protected final List<Component> children;
   protected final Style style;

   protected AbstractComponent(@NotNull List<? extends ComponentLike> children, @NotNull Style style) {
      this.children = ComponentLike.asComponents(children, NOT_EMPTY);
      this.style = style;
   }

   @NotNull
   public final List<Component> children() {
      return this.children;
   }

   @NotNull
   public final Style style() {
      return this.style;
   }

   @NotNull
   public Component replaceText(@NotNull Consumer<TextReplacementConfig.Builder> configurer) {
      Objects.requireNonNull(configurer, "configurer");
      return this.replaceText((TextReplacementConfig)Buildable.configureAndBuild(TextReplacementConfig.builder(), configurer));
   }

   @NotNull
   public Component replaceText(@NotNull TextReplacementConfig config) {
      Objects.requireNonNull(config, "replacement");
      if (!(config instanceof TextReplacementConfigImpl)) {
         throw new IllegalArgumentException("Provided replacement was a custom TextReplacementConfig implementation, which is not supported.");
      } else {
         return TextReplacementRenderer.INSTANCE.render(this, ((TextReplacementConfigImpl)config).createState());
      }
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof AbstractComponent)) {
         return false;
      } else {
         AbstractComponent that = (AbstractComponent)other;
         return Objects.equals(this.children, that.children) && Objects.equals(this.style, that.style);
      }
   }

   public int hashCode() {
      int result = this.children.hashCode();
      result = 31 * result + this.style.hashCode();
      return result;
   }

   private String debuggerString() {
      return (String)StringExaminer.simpleEscaping().examine(this.examinableName(), this.examinablePropertiesWithoutChildren());
   }

   protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
      return Stream.of(ExaminableProperty.of("style", (Object)this.style));
   }

   @NotNull
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.concat(this.examinablePropertiesWithoutChildren(), Stream.of(ExaminableProperty.of("children", (Object)this.children)));
   }

   public String toString() {
      return (String)this.examine(StringExaminer.simpleEscaping());
   }
}
