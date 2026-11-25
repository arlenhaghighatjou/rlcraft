package me.lucko.spark.lib.adventure.text.format;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEventSource;
import me.lucko.spark.lib.adventure.util.Buildable;
import me.lucko.spark.lib.adventure.util.MonkeyBars;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface Style extends Buildable<Style, Style.Builder>, Examinable {
   Key DEFAULT_FONT = Key.key("default");

   @NotNull
   static Style empty() {
      return StyleImpl.EMPTY;
   }

   @NotNull
   static Style.Builder style() {
      return new StyleImpl.BuilderImpl();
   }

   @NotNull
   static Style style(@NotNull Consumer<Style.Builder> consumer) {
      return (Style)Buildable.configureAndBuild(style(), consumer);
   }

   @NotNull
   static Style style(@Nullable TextColor color) {
      return (Style)(color == null ? empty() : new StyleImpl((Key)null, color, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, (ClickEvent)null, (HoverEvent)null, (String)null));
   }

   @NotNull
   static Style style(@NotNull TextDecoration decoration) {
      return style().decoration(decoration, true).build();
   }

   @NotNull
   static Style style(@Nullable TextColor color, @NotNull TextDecoration... decorations) {
      Style.Builder builder = style();
      builder.color(color);
      StyleImpl.decorate(builder, decorations);
      return builder.build();
   }

   @NotNull
   static Style style(@Nullable TextColor color, Set<TextDecoration> decorations) {
      Style.Builder builder = style();
      builder.color(color);
      if (!decorations.isEmpty()) {
         for(TextDecoration decoration : decorations) {
            builder.decoration(decoration, true);
         }
      }

      return builder.build();
   }

   @NotNull
   static Style style(@NotNull StyleBuilderApplicable... applicables) {
      if (applicables.length == 0) {
         return empty();
      } else {
         Style.Builder builder = style();
         int i = 0;

         for(int length = applicables.length; i < length; ++i) {
            applicables[i].styleApply(builder);
         }

         return builder.build();
      }
   }

   @NotNull
   static Style style(@NotNull Iterable<? extends StyleBuilderApplicable> applicables) {
      Style.Builder builder = style();

      for(StyleBuilderApplicable applicable : applicables) {
         applicable.styleApply(builder);
      }

      return builder.build();
   }

   @NotNull
   default Style edit(@NotNull Consumer<Style.Builder> consumer) {
      return this.edit(consumer, Style.Merge.Strategy.ALWAYS);
   }

   @NotNull
   default Style edit(@NotNull Consumer<Style.Builder> consumer, @NotNull Style.Merge.Strategy strategy) {
      return style((style) -> {
         if (strategy == Style.Merge.Strategy.ALWAYS) {
            style.merge(this, strategy);
         }

         consumer.accept(style);
         if (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET) {
            style.merge(this, strategy);
         }

      });
   }

   @Nullable
   Key font();

   @NotNull
   Style font(@Nullable Key var1);

   @Nullable
   TextColor color();

   @NotNull
   Style color(@Nullable TextColor var1);

   @NotNull
   Style colorIfAbsent(@Nullable TextColor var1);

   default boolean hasDecoration(@NotNull TextDecoration decoration) {
      return this.decoration(decoration) == TextDecoration.State.TRUE;
   }

   @NotNull
   TextDecoration.State decoration(@NotNull TextDecoration var1);

   @NotNull
   default Style decorate(@NotNull TextDecoration decoration) {
      return this.decoration(decoration, TextDecoration.State.TRUE);
   }

   @NotNull
   default Style decoration(@NotNull TextDecoration decoration, boolean flag) {
      return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
   }

   @NotNull
   Style decoration(@NotNull TextDecoration var1, @NotNull TextDecoration.State var2);

   @NotNull
   @Unmodifiable
   Map<TextDecoration, TextDecoration.State> decorations();

   @NotNull
   Style decorations(@NotNull Map<TextDecoration, TextDecoration.State> var1);

   @Nullable
   ClickEvent clickEvent();

   @NotNull
   Style clickEvent(@Nullable ClickEvent var1);

   @Nullable
   HoverEvent<?> hoverEvent();

   @NotNull
   Style hoverEvent(@Nullable HoverEventSource<?> var1);

   @Nullable
   String insertion();

   @NotNull
   Style insertion(@Nullable String var1);

   @NotNull
   default Style merge(@NotNull Style that) {
      return this.merge(that, Style.Merge.ALL);
   }

   @NotNull
   default Style merge(@NotNull Style that, @NotNull Style.Merge.Strategy strategy) {
      return this.merge(that, strategy, Style.Merge.ALL);
   }

   @NotNull
   default Style merge(@NotNull Style that, @NotNull Style.Merge merge) {
      return this.merge(that, Collections.singleton(merge));
   }

   @NotNull
   default Style merge(@NotNull Style that, @NotNull Style.Merge.Strategy strategy, @NotNull Style.Merge merge) {
      return this.merge(that, strategy, Collections.singleton(merge));
   }

   @NotNull
   default Style merge(@NotNull Style that, @NotNull Style.Merge... merges) {
      return this.merge(that, Style.Merge.of(merges));
   }

   @NotNull
   default Style merge(@NotNull Style that, @NotNull Style.Merge.Strategy strategy, @NotNull Style.Merge... merges) {
      return this.merge(that, strategy, Style.Merge.of(merges));
   }

   @NotNull
   default Style merge(@NotNull Style that, @NotNull Set<Style.Merge> merges) {
      return this.merge(that, Style.Merge.Strategy.ALWAYS, merges);
   }

   @NotNull
   Style merge(@NotNull Style var1, @NotNull Style.Merge.Strategy var2, @NotNull Set<Style.Merge> var3);

   boolean isEmpty();

   @NotNull
   Style.Builder toBuilder();

   public interface Builder extends Buildable.Builder<Style> {
      @Contract("_ -> this")
      @NotNull
      Style.Builder font(@Nullable Key var1);

      @Contract("_ -> this")
      @NotNull
      Style.Builder color(@Nullable TextColor var1);

      @Contract("_ -> this")
      @NotNull
      Style.Builder colorIfAbsent(@Nullable TextColor var1);

      @Contract("_ -> this")
      @NotNull
      default Style.Builder decorate(@NotNull TextDecoration decoration) {
         return this.decoration(decoration, TextDecoration.State.TRUE);
      }

      @Contract("_ -> this")
      @NotNull
      default Style.Builder decorate(@NotNull TextDecoration... decorations) {
         int i = 0;

         for(int length = decorations.length; i < length; ++i) {
            this.decorate(decorations[i]);
         }

         return this;
      }

      @Contract("_, _ -> this")
      @NotNull
      default Style.Builder decoration(@NotNull TextDecoration decoration, boolean flag) {
         return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
      }

      @Contract("_, _ -> this")
      @NotNull
      Style.Builder decoration(@NotNull TextDecoration var1, @NotNull TextDecoration.State var2);

      @Contract("_ -> this")
      @NotNull
      Style.Builder clickEvent(@Nullable ClickEvent var1);

      @Contract("_ -> this")
      @NotNull
      Style.Builder hoverEvent(@Nullable HoverEventSource<?> var1);

      @Contract("_ -> this")
      @NotNull
      Style.Builder insertion(@Nullable String var1);

      @Contract("_ -> this")
      @NotNull
      default Style.Builder merge(@NotNull Style that) {
         return this.merge(that, Style.Merge.ALL);
      }

      @Contract("_, _ -> this")
      @NotNull
      default Style.Builder merge(@NotNull Style that, @NotNull Style.Merge.Strategy strategy) {
         return this.merge(that, strategy, Style.Merge.ALL);
      }

      @Contract("_, _ -> this")
      @NotNull
      default Style.Builder merge(@NotNull Style that, @NotNull Style.Merge... merges) {
         return merges.length == 0 ? this : this.merge(that, Style.Merge.of(merges));
      }

      @Contract("_, _, _ -> this")
      @NotNull
      default Style.Builder merge(@NotNull Style that, @NotNull Style.Merge.Strategy strategy, @NotNull Style.Merge... merges) {
         return merges.length == 0 ? this : this.merge(that, strategy, Style.Merge.of(merges));
      }

      @Contract("_, _ -> this")
      @NotNull
      default Style.Builder merge(@NotNull Style that, @NotNull Set<Style.Merge> merges) {
         return this.merge(that, Style.Merge.Strategy.ALWAYS, merges);
      }

      @Contract("_, _, _ -> this")
      @NotNull
      Style.Builder merge(@NotNull Style var1, @NotNull Style.Merge.Strategy var2, @NotNull Set<Style.Merge> var3);

      @Contract("_ -> this")
      @NotNull
      default Style.Builder apply(@NotNull StyleBuilderApplicable applicable) {
         applicable.styleApply(this);
         return this;
      }

      @NotNull
      Style build();
   }

   public static enum Merge {
      COLOR,
      DECORATIONS,
      EVENTS,
      INSERTION,
      FONT;

      static final Set<Style.Merge> ALL = of(values());
      static final Set<Style.Merge> COLOR_AND_DECORATIONS = of(COLOR, DECORATIONS);

      private Merge() {
      }

      @NotNull
      @Unmodifiable
      public static Set<Style.Merge> all() {
         return ALL;
      }

      @NotNull
      @Unmodifiable
      public static Set<Style.Merge> colorAndDecorations() {
         return COLOR_AND_DECORATIONS;
      }

      @NotNull
      @Unmodifiable
      public static Set<Style.Merge> of(@NotNull Style.Merge... merges) {
         return MonkeyBars.<Style.Merge>enumSet(Style.Merge.class, merges);
      }

      static boolean hasAll(@NotNull Set<Style.Merge> merges) {
         return merges.size() == ALL.size();
      }

      public static enum Strategy {
         ALWAYS,
         NEVER,
         IF_ABSENT_ON_TARGET;

         private Strategy() {
         }
      }
   }
}
