package hunternif.mc.atlas.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class FileUtil {
   private static final JsonParser parser = new JsonParser();
   private static final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

   FileUtil() {
   }

   public static JsonElement readJson(File file) {
      JsonElement root = null;
      InputStream input = null;

      InputStreamReader e;
      try {
         if (file.exists()) {
            input = new FileInputStream(file);
            reader = new InputStreamReader(input);
            root = parser.parse(e);
            return root;
         }

         file.createNewFile();
         e = null;
      } catch (FileNotFoundException var18) {
         Log.error(var18, "Error creating file %s", file.getName());
         return root;
      } catch (IOException var19) {
         Log.error(var19, "Error opening file %s", file.getName());
         return root;
      } catch (JsonSyntaxException | JsonIOException var20) {
         Log.error(var20, "Error parsing file %s", file.getName());
         return root;
      } finally {
         if (input != null) {
            try {
               input.close();
            } catch (IOException var17) {
               Log.error(var17, "Error reading file %s", file.getName());
            }
         }

      }

      return e;
   }

   public static void writeJson(JsonElement root, File file) {
      BufferedWriter writer = null;

      try {
         if (!file.exists()) {
            file.createNewFile();
         }

         writer = new BufferedWriter(new FileWriter(file));
         gson.toJson(root, writer);
      } catch (IOException var14) {
         Log.error(var14, "Error opening file %s", file.getName());
      } catch (JsonIOException var15) {
         Log.error(var15, "Error writing file %s", file.getName());
      } finally {
         if (writer != null) {
            try {
               writer.close();
            } catch (IOException var13) {
               Log.error(var13, "Error writing file %s", file.getName());
            }
         }

      }

   }
}
