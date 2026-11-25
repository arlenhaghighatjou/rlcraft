package me.lucko.spark.lib.adventure.translation;

import java.util.Locale;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.renderer.TranslatableComponentRenderer;
import org.jetbrains.annotations.NotNull;

public interface GlobalTranslator extends Translator, Examinable {
   @NotNull
   static GlobalTranslator get() {
      return GlobalTranslatorImpl.INSTANCE;
   }

   @NotNull
   static TranslatableComponentRenderer<Locale> renderer() {
      return GlobalTranslatorImpl.INSTANCE.renderer;
   }

   @NotNull
   static Component render(@NotNull Component component, @NotNull Locale locale) {
      return renderer().render(component, locale);
   }

   @NotNull
   Iterable<? extends Translator> sources();

   boolean addSource(@NotNull Translator var1);

   boolean removeSource(@NotNull Translator var1);
}
