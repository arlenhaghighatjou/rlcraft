package hunternif.mc.atlas.util;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

class FileUtil {
   private static final JsonParser parser = new JsonParser();
   private static final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

   FileUtil() {
   }

   public static JsonElement readJson(VFile2 file) {
      JsonElement root = null;

      try {
         String chars = file.getAllChars();
         if (chars != null) {
            root = parser.parse(chars);
            return root;
         }

         file.setAllBytes(new byte[0]);
         return null;
      } catch (JsonSyntaxException | JsonIOException var20) {
         Log.error(var20, "Error parsing file %s", file.getName());
         return root;
      }
   }

   public static void writeJson(JsonElement root, VFile2 file) {
      try {
         file.setAllChars(gson.toJson(root));
      } catch (JsonIOException var15) {
         Log.error(var15, "Error writing file %s", file.getName());
      }

   }
}
