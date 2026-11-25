package me.lucko.spark.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public final class Configuration {
   private static final JsonParser PARSER = new JsonParser();
   private final JsonObject root;

   public Configuration(Path file) {
      JsonObject root = null;
      if (Files.exists(file, new LinkOption[0])) {
         try {
            BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);

            try {
               root = PARSER.parse(reader).getAsJsonObject();
            } catch (Throwable var7) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (reader != null) {
               reader.close();
            }
         } catch (IOException var8) {
            var8.printStackTrace();
         }
      }

      this.root = root != null ? root : new JsonObject();
   }

   public String getString(String path, String def) {
      JsonElement el = this.root.get(path);
      return el != null && el.isJsonPrimitive() ? el.getAsJsonPrimitive().getAsString() : def;
   }

   public boolean getBoolean(String path, boolean def) {
      JsonElement el = this.root.get(path);
      if (el != null && el.isJsonPrimitive()) {
         JsonPrimitive val = el.getAsJsonPrimitive();
         return val.isBoolean() ? val.getAsBoolean() : def;
      } else {
         return def;
      }
   }
}
