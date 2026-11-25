package org.orecruncher.lib;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class JsonUtils {
   private JsonUtils() {
   }

   @Nullable
   public static <T> T loadFromJar(@Nonnull Class<T> clazz, @Nonnull String path) throws Exception {
      try {
         InputStream stream = clazz.getResourceAsStream(path);
         Throwable var17 = null;

         Object var4;
         try {
            var4 = load(stream, clazz);
         } catch (Throwable var14) {
            var17 = var14;
            throw var14;
         } finally {
            if (stream != null) {
               if (var17 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var13) {
                     var17.addSuppressed(var13);
                  }
               } else {
                  stream.close();
               }
            }

         }

         return (T)var4;
      } catch (Throwable var16) {
         String msg = String.format("Unable to load resource [%s] from JAR", path);
         LibLog.log().error(msg, var16);
         return (T)clazz.newInstance();
      }
   }

   @Nullable
   public static <T> T load(@Nonnull InputStream stream, @Nonnull Class<T> clazz) throws Exception {
      try {
         InputStreamReader reader = new InputStreamReader(stream);
         Throwable var3 = null;

         Object var4;
         try {
            var4 = (new Gson()).fromJson(reader, clazz);
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (reader != null) {
               if (var3 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  reader.close();
               }
            }

         }

         return (T)var4;
      } catch (Throwable var16) {
         LibLog.log().error("Unable to process Json from stream", var16);
         return (T)clazz.newInstance();
      }
   }

   public static class JsonValidationFailed extends RuntimeException {
      private static final long serialVersionUID = 1L;

      public JsonValidationFailed(@Nonnull String cause, @Nonnull Throwable t) {
         super(cause, t);
      }
   }
}
