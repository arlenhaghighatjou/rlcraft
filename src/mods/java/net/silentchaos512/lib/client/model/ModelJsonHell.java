package net.silentchaos512.lib.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraftforge.fml.common.Loader;

public final class ModelJsonHell {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

   private ModelJsonHell() {
      throw new IllegalAccessError("Utility class");
   }

   public static void createItemModel(String fileName, String... textures) {
      Map<String, Object> json = new HashMap();
      Map<String, Object> texList = new LinkedHashMap();

      for(int i = 0; i < textures.length; ++i) {
         texList.put("layer" + i, textures[i]);
      }

      json.put("parent", "item/generated");
      json.put("textures", texList);
      createModelFile(json, "item", fileName);
   }

   private static void createModelFile(Map<String, Object> json, String subFolder, String fileName) {
      fileName = fileName.replaceAll(":", "_");
      File directory = new File("models/" + subFolder + "/" + Loader.instance().activeModContainer().getModId());
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File output = new File(directory, fileName + ".json");

      try {
         FileWriter writer = new FileWriter(output);
         Throwable var6 = null;

         try {
            GSON.toJson(json, writer);
            writer.close();
         } catch (Throwable var16) {
            var6 = var16;
            throw var16;
         } finally {
            if (writer != null) {
               if (var6 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var15) {
                     var6.addSuppressed(var15);
                  }
               } else {
                  writer.close();
               }
            }

         }
      } catch (IOException var18) {
         var18.printStackTrace();
      }

   }
}
