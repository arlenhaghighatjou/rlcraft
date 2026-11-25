package me.lucko.spark.lib.adventure.text.renderer;

import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.MessageFormat.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import me.lucko.spark.lib.adventure.text.BlockNBTComponent;
import me.lucko.spark.lib.adventure.text.BuildableComponent;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.ComponentBuilder;
import me.lucko.spark.lib.adventure.text.EntityNBTComponent;
import me.lucko.spark.lib.adventure.text.KeybindComponent;
import me.lucko.spark.lib.adventure.text.NBTComponent;
import me.lucko.spark.lib.adventure.text.NBTComponentBuilder;
import me.lucko.spark.lib.adventure.text.ScoreComponent;
import me.lucko.spark.lib.adventure.text.SelectorComponent;
import me.lucko.spark.lib.adventure.text.StorageNBTComponent;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.TranslatableComponent;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TranslatableComponentRenderer<C> extends AbstractComponentRenderer<C> {
   private static final Set<Style.Merge> MERGES = Style.Merge.of(Style.Merge.COLOR, Style.Merge.DECORATIONS, Style.Merge.INSERTION, Style.Merge.FONT);

   public TranslatableComponentRenderer() {
   }

   @NotNull
   public static TranslatableComponentRenderer<Locale> usingTranslationSource(@NotNull final Translator source) {
      Objects.requireNonNull(source, "source");
      return new TranslatableComponentRenderer<Locale>() {
         @Nullable
         protected MessageFormat translate(@NotNull String key, @NotNull Locale context) {
            return source.translate(key, context);
         }
      };
   }

   @Nullable
   protected abstract MessageFormat translate(@NotNull String var1, @NotNull C var2);

   @NotNull
   protected Component renderBlockNbt(@NotNull BlockNBTComponent component, @NotNull C context) {
      BlockNBTComponent.Builder builder = ((BlockNBTComponent.Builder)nbt(Component.blockNBT(), component)).pos(component.pos());
      return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
   }

   @NotNull
   protected Component renderEntityNbt(@NotNull EntityNBTComponent component, @NotNull C context) {
      EntityNBTComponent.Builder builder = ((EntityNBTComponent.Builder)nbt(Component.entityNBT(), component)).selector(component.selector());
      return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
   }

   @NotNull
   protected Component renderStorageNbt(@NotNull StorageNBTComponent component, @NotNull C context) {
      StorageNBTComponent.Builder builder = ((StorageNBTComponent.Builder)nbt(Component.storageNBT(), component)).storage(component.storage());
      return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
   }

   protected static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(B builder, C oldComponent) {
      return (B)builder.nbtPath(oldComponent.nbtPath()).interpret(oldComponent.interpret());
   }

   @NotNull
   protected Component renderKeybind(@NotNull KeybindComponent component, @NotNull C context) {
      KeybindComponent.Builder builder = Component.keybind().keybind(component.keybind());
      return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
   }

   @NotNull
   protected Component renderScore(@NotNull ScoreComponent component, @NotNull C context) {
      ScoreComponent.Builder builder = Component.score().name(component.name()).objective(component.objective()).value(component.value());
      return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
   }

   @NotNull
   protected Component renderSelector(@NotNull SelectorComponent component, @NotNull C context) {
      SelectorComponent.Builder builder = Component.selector().pattern(component.pattern());
      return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
   }

   @NotNull
   protected Component renderText(@NotNull TextComponent component, @NotNull C context) {
      TextComponent.Builder builder = Component.text().content(component.content());
      return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
   }

   @NotNull
   protected Component renderTranslatable(@NotNull TranslatableComponent component, @NotNull C context) {
      MessageFormat format = this.translate(component.key(), context);
      if (format != null) {
         List<Component> args = component.args();
         TextComponent.Builder builder = Component.text();
         this.mergeStyle(component, builder, context);
         if (args.isEmpty()) {
            builder.content(format.format((Object[])null, new StringBuffer(), (FieldPosition)null).toString());
            return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
         } else {
            Object[] nulls = new Object[args.size()];
            StringBuffer sb = format.format(nulls, new StringBuffer(), (FieldPosition)null);

            int end;
            for(AttributedCharacterIterator it = format.formatToCharacterIterator(nulls); it.getIndex() < it.getEndIndex(); it.setIndex(end)) {
               end = it.getRunLimit();
               Integer index = (Integer)it.getAttribute(Field.ARGUMENT);
               if (index != null) {
                  builder.append(this.render((Component)args.get(index.intValue()), context));
               } else {
                  builder.append(Component.text(sb.substring(it.getIndex(), end)));
               }
            }

            return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
         }
      } else {
         TranslatableComponent.Builder builder = Component.translatable().key(component.key());
         if (!component.args().isEmpty()) {
            List<Component> args = new ArrayList(component.args());
            int i = 0;

            for(int size = args.size(); i < size; ++i) {
               args.set(i, this.render((Component)args.get(i), context));
            }

            builder.args(args);
         }

         return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
      }
   }

   protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O mergeStyleAndOptionallyDeepRender(Component component, B builder, C context) {
      this.mergeStyle(component, builder, context);
      return (O)this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
   }

   protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O optionallyRenderChildrenAppendAndBuild(List<Component> children, B builder, C context) {
      if (!children.isEmpty()) {
         children.forEach((child) -> {
            builder.append(this.render(child, context));
         });
      }

      return (O)builder.build();
   }

   protected <B extends ComponentBuilder<?, ?>> void mergeStyle(Component component, B builder, C context) {
      builder.mergeStyle(component, MERGES);
      builder.clickEvent(component.clickEvent());
      HoverEvent<?> hoverEvent = component.hoverEvent();
      if (hoverEvent != null) {
         builder.hoverEvent(hoverEvent.withRenderedValue(this, context));
      }

   }
}
