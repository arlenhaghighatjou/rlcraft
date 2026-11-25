package me.lucko.spark.lib.adventure.text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class KeybindComponentImpl extends AbstractComponent implements KeybindComponent {
   private final String keybind;

   KeybindComponentImpl(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String keybind) {
      super(children, style);
      this.keybind = (String)Objects.requireNonNull(keybind, "keybind");
   }

   @NotNull
   public String keybind() {
      return this.keybind;
   }

   @NotNull
   public KeybindComponent keybind(@NotNull String keybind) {
      return Objects.equals(this.keybind, keybind) ? this : new KeybindComponentImpl(this.children, this.style, (String)Objects.requireNonNull(keybind, "keybind"));
   }

   @NotNull
   public KeybindComponent children(@NotNull List<? extends ComponentLike> children) {
      return new KeybindComponentImpl(children, this.style, this.keybind);
   }

   @NotNull
   public KeybindComponent style(@NotNull Style style) {
      return new KeybindComponentImpl(this.children, style, this.keybind);
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof KeybindComponent)) {
         return false;
      } else if (!super.equals(other)) {
         return false;
      } else {
         KeybindComponent that = (KeybindComponent)other;
         return Objects.equals(this.keybind, that.keybind());
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + this.keybind.hashCode();
      return result;
   }

   @NotNull
   protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
      return Stream.concat(Stream.of(ExaminableProperty.of("keybind", this.keybind)), super.examinablePropertiesWithoutChildren());
   }

   @NotNull
   public KeybindComponent.Builder toBuilder() {
      return new KeybindComponentImpl.BuilderImpl(this);
   }

   static final class BuilderImpl extends AbstractComponentBuilder<KeybindComponent, KeybindComponent.Builder> implements KeybindComponent.Builder {
      @Nullable
      private String keybind;

      BuilderImpl() {
      }

      BuilderImpl(@NotNull KeybindComponent component) {
         super(component);
         this.keybind = component.keybind();
      }

      @NotNull
      public KeybindComponent.Builder keybind(@NotNull String keybind) {
         this.keybind = keybind;
         return this;
      }

      @NotNull
      public KeybindComponent build() {
         if (this.keybind == null) {
            throw new IllegalStateException("keybind must be set");
         } else {
            return new KeybindComponentImpl(this.children, this.buildStyle(), this.keybind);
         }
      }
   }
}
