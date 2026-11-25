package c4.champions.common.util;

import c4.champions.Champions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

public class JsonUtil {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

   public JsonUtil() {
   }

   public static <T> T[] fromJson(@Nonnull TypeToken<T[]> token, @Nonnull File file, @Nonnull T[] defaults) {
      if (!file.exists()) {
         toJson(token, file, defaults);
         return defaults;
      } else {
         try {
            FileReader reader = new FileReader(file);
            Throwable var4 = null;

            Object[] var5;
            try {
               var5 = GSON.fromJson(reader, token.getType());
            } catch (Throwable var15) {
               var4 = var15;
               throw var15;
            } finally {
               if (reader != null) {
                  if (var4 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var14) {
                        var4.addSuppressed(var14);
                     }
                  } else {
                     reader.close();
                  }
               }

            }

            return (T[])var5;
         } catch (IOException var17) {
            Champions.logger.log(Level.ERROR, "Error reading Json file");
            return defaults;
         }
      }
   }

   private static <T> void toJson(@Nonnull TypeToken<T[]> token, @Nonnull File file, @Nonnull T[] defaults) {
      if (!file.exists()) {
         try {
            FileUtils.forceMkdirParent(file);
            file.createNewFile();
         } catch (IOException var16) {
            Champions.logger.log(Level.ERROR, "Error creating Json file");
            return;
         }
      }

      try {
         FileWriter writer = new FileWriter(file);
         Throwable var4 = null;

         try {
            writer.write(getJson(defaults, token));
         } catch (Throwable var15) {
            var4 = var15;
            throw var15;
         } finally {
            if (writer != null) {
               if (var4 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var14) {
                     var4.addSuppressed(var14);
                  }
               } else {
                  writer.close();
               }
            }

         }
      } catch (IOException var18) {
         Champions.logger.log(Level.ERROR, "Error creating Json file");
      }

   }

   private static <T> String getJson(@Nonnull T[] elements, @Nonnull TypeToken<T[]> token) {
      return GSON.toJson(elements, token.getType());
   }
}
