package me.lucko.spark.lib.adventure.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import me.lucko.spark.lib.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TranslationRegistry extends Translator {
   Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("'");

   @NotNull
   static TranslationRegistry create(Key name) {
      return new TranslationRegistryImpl((Key)Objects.requireNonNull(name, "name"));
   }

   boolean contains(@NotNull String var1);

   @Nullable
   MessageFormat translate(@NotNull String var1, @NotNull Locale var2);

   void defaultLocale(@NotNull Locale var1);

   void register(@NotNull String var1, @NotNull Locale var2, @NotNull MessageFormat var3);

   default void registerAll(@NotNull Locale locale, @NotNull Map<String, MessageFormat> formats) {
      Set var10002 = formats.keySet();
      Objects.requireNonNull(formats);
      this.registerAll(locale, var10002, formats::get);
   }

   default void registerAll(@NotNull Locale locale, @NotNull Path path, boolean escapeSingleQuotes) {
      try {
         BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

         try {
            this.registerAll(locale, new PropertyResourceBundle(reader), escapeSingleQuotes);
         } catch (Throwable var8) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (reader != null) {
            reader.close();
         }
      } catch (IOException var9) {
         ;
      }

   }

   default void registerAll(@NotNull Locale locale, @NotNull ResourceBundle bundle, boolean escapeSingleQuotes) {
      this.registerAll(locale, bundle.keySet(), (key) -> {
         String format = bundle.getString(key);
         return new MessageFormat(escapeSingleQuotes ? SINGLE_QUOTE_PATTERN.matcher(format).replaceAll("''") : format, locale);
      });
   }

   default void registerAll(@NotNull Locale locale, @NotNull Set<String> keys, Function<String, MessageFormat> function) {
      List<IllegalArgumentException> errors = null;

      for(String key : keys) {
         try {
            this.register(key, locale, (MessageFormat)function.apply(key));
         } catch (IllegalArgumentException var8) {
            if (errors == null) {
               errors = new LinkedList();
            }

            errors.add(var8);
         }
      }

      if (errors != null) {
         int size = errors.size();
         if (size == 1) {
            throw (IllegalArgumentException)errors.get(0);
         }

         if (size > 1) {
            throw new IllegalArgumentException(String.format("Invalid key (and %d more)", size - 1), (Throwable)errors.get(0));
         }
      }

   }

   void unregister(@NotNull String var1);
}
