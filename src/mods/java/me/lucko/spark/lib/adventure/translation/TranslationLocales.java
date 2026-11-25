package me.lucko.spark.lib.adventure.translation;

import java.util.Locale;
import java.util.function.Supplier;

final class TranslationLocales {
   private static final Supplier<Locale> GLOBAL;

   private TranslationLocales() {
   }

   static Locale global() {
      return (Locale)GLOBAL.get();
   }

   static {
      String property = System.getProperty("net.kyo".concat("ri.adventure.defaultTranslationLocale"));
      if (property != null && !property.isEmpty()) {
         if (property.equals("system")) {
            GLOBAL = Locale::getDefault;
         } else {
            Locale locale = Translator.parseLocale(property);
            GLOBAL = () -> {
               return locale;
            };
         }
      } else {
         GLOBAL = () -> {
            return Locale.US;
         };
      }

   }
}
