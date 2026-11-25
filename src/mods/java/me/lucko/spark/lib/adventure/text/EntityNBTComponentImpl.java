package me.lucko.spark.lib.adventure.text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class EntityNBTComponentImpl extends NBTComponentImpl<EntityNBTComponent, EntityNBTComponent.Builder> implements EntityNBTComponent {
   private final String selector;

   EntityNBTComponentImpl(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator, String selector) {
      super(children, style, nbtPath, interpret, separator);
      this.selector = selector;
   }

   @NotNull
   public EntityNBTComponent nbtPath(@NotNull String nbtPath) {
      return Objects.equals(this.nbtPath, nbtPath) ? this : new EntityNBTComponentImpl(this.children, this.style, nbtPath, this.interpret, this.separator, this.selector);
   }

   @NotNull
   public EntityNBTComponent interpret(boolean interpret) {
      return this.interpret == interpret ? this : new EntityNBTComponentImpl(this.children, this.style, this.nbtPath, interpret, this.separator, this.selector);
   }

   @Nullable
   public Component separator() {
      return this.separator;
   }

   @NotNull
   public EntityNBTComponent separator(@Nullable ComponentLike separator) {
      return new EntityNBTComponentImpl(this.children, this.style, this.nbtPath, this.interpret, separator, this.selector);
   }

   @NotNull
   public String selector() {
      return this.selector;
   }

   @NotNull
   public EntityNBTComponent selector(@NotNull String selector) {
      return Objects.equals(this.selector, selector) ? this : new EntityNBTComponentImpl(this.children, this.style, this.nbtPath, this.interpret, this.separator, selector);
   }

   @NotNull
   public EntityNBTComponent children(@NotNull List<? extends ComponentLike> children) {
      return new EntityNBTComponentImpl(children, this.style, this.nbtPath, this.interpret, this.separator, this.selector);
   }

   @NotNull
   public EntityNBTComponent style(@NotNull Style style) {
      return new EntityNBTComponentImpl(this.children, style, this.nbtPath, this.interpret, this.separator, this.selector);
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof EntityNBTComponent)) {
         return false;
      } else if (!super.equals(other)) {
         return false;
      } else {
         EntityNBTComponentImpl that = (EntityNBTComponentImpl)other;
         return Objects.equals(this.selector, that.selector());
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + this.selector.hashCode();
      return result;
   }

   @NotNull
   protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
      return Stream.concat(Stream.of(ExaminableProperty.of("selector", this.selector)), super.examinablePropertiesWithoutChildren());
   }

   @NotNull
   public EntityNBTComponent.Builder toBuilder() {
      return new EntityNBTComponentImpl.BuilderImpl(this);
   }

   static final class BuilderImpl extends NBTComponentImpl.BuilderImpl<EntityNBTComponent, EntityNBTComponent.Builder> implements EntityNBTComponent.Builder {
      @Nullable
      private String selector;

      BuilderImpl() {
      }

      BuilderImpl(@NotNull EntityNBTComponent component) {
         super(component);
         this.selector = component.selector();
      }

      @NotNull
      public EntityNBTComponent.Builder selector(@NotNull String selector) {
         this.selector = selector;
         return this;
      }

      @NotNull
      public EntityNBTComponent build() {
         if (this.nbtPath == null) {
            throw new IllegalStateException("nbt path must be set");
         } else if (this.selector == null) {
            throw new IllegalStateException("selector must be set");
         } else {
            return new EntityNBTComponentImpl(this.children, this.buildStyle(), this.nbtPath, this.interpret, this.separator, this.selector);
         }
      }
   }
}
