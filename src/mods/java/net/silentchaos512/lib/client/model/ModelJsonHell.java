package net.silentchaos512.lib.client.model;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
      VFile2 output = new VFile2("models/" + subFolder + "/" + Loader.instance().activeModContainer().getModId(), fileName + ".json");
      output.setAllChars(GSON.toJson(json));
   }
}
