package me.lucko.spark.lib.adventure.text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TranslatableComponentImpl extends AbstractComponent implements TranslatableComponent {
   private final String key;
   private final List<Component> args;

   TranslatableComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @NotNull ComponentLike[] args) {
      this(children, style, key, Arrays.asList(args));
   }

   TranslatableComponentImpl(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String key, @NotNull List<? extends ComponentLike> args) {
      super(children, style);
      this.key = (String)Objects.requireNonNull(key, "key");
      this.args = ComponentLike.asComponents(args);
   }

   @NotNull
   public String key() {
      return this.key;
   }

   @NotNull
   public TranslatableComponent key(@NotNull String key) {
      return Objects.equals(this.key, key) ? this : new TranslatableComponentImpl(this.children, this.style, (String)Objects.requireNonNull(key, "key"), this.args);
   }

   @NotNull
   public List<Component> args() {
      return this.args;
   }

   @NotNull
   public TranslatableComponent args(@NotNull ComponentLike... args) {
      return new TranslatableComponentImpl(this.children, this.style, this.key, args);
   }

   @NotNull
   public TranslatableComponent args(@NotNull List<? extends ComponentLike> args) {
      return new TranslatableComponentImpl(this.children, this.style, this.key, args);
   }

   @NotNull
   public TranslatableComponent children(@NotNull List<? extends ComponentLike> children) {
      return new TranslatableComponentImpl(children, this.style, this.key, this.args);
   }

   @NotNull
   public TranslatableComponent style(@NotNull Style style) {
      return new TranslatableComponentImpl(this.children, style, this.key, this.args);
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof TranslatableComponent)) {
         return false;
      } else if (!super.equals(other)) {
         return false;
      } else {
         TranslatableComponent that = (TranslatableComponent)other;
         return Objects.equals(this.key, that.key()) && Objects.equals(this.args, that.args());
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + this.key.hashCode();
      result = 31 * result + this.args.hashCode();
      return result;
   }

   @NotNull
   protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
      return Stream.concat(Stream.of(ExaminableProperty.of("key", this.key), ExaminableProperty.of("args", (Object)this.args)), super.examinablePropertiesWithoutChildren());
   }

   @NotNull
   public TranslatableComponent.Builder toBuilder() {
      return new TranslatableComponentImpl.BuilderImpl(this);
   }

   static final class BuilderImpl extends AbstractComponentBuilder<TranslatableComponent, TranslatableComponent.Builder> implements TranslatableComponent.Builder {
      @Nullable
      private String key;
      private List<? extends Component> args = Collections.emptyList();

      BuilderImpl() {
      }

      BuilderImpl(@NotNull TranslatableComponent component) {
         super(component);
         this.key = component.key();
         this.args = component.args();
      }

      @NotNull
      public TranslatableComponent.Builder key(@NotNull String key) {
         this.key = key;
         return this;
      }

      @NotNull
      public TranslatableComponent.Builder args(@NotNull ComponentBuilder<?, ?> arg) {
         return this.args(Collections.singletonList(arg.build()));
      }

      @NotNull
      public TranslatableComponent.Builder args(@NotNull ComponentBuilder<?, ?>... args) {
         return args.length == 0 ? this.args(Collections.emptyList()) : this.args((List)Stream.of(args).map(ComponentBuilder::build).collect(Collectors.toList()));
      }

      @NotNull
      public TranslatableComponent.Builder args(@NotNull Component arg) {
         return this.args(Collections.singletonList(arg));
      }

      @NotNull
      public TranslatableComponent.Builder args(@NotNull ComponentLike... args) {
         return args.length == 0 ? this.args(Collections.emptyList()) : this.args(Arrays.asList(args));
      }

      @NotNull
      public TranslatableComponent.Builder args(@NotNull List<? extends ComponentLike> args) {
         this.args = ComponentLike.asComponents(args);
         return this;
      }

      @NotNull
      public TranslatableComponentImpl build() {
         if (this.key == null) {
            throw new IllegalStateException("key must be set");
         } else {
            return new TranslatableComponentImpl(this.children, this.buildStyle(), this.key, this.args);
         }
      }
   }
}
