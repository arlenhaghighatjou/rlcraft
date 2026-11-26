package net.silentchaos512.lib.util;

import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Loader;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class JsonLoader {
   private static final Gson GSON = (new GsonBuilder()).create();

   public JsonLoader() {
   }

   public static String getAssetsPath(ResourceLocation name, String subfolder) {
      return "assets/" + name.getResourceDomain() + "/" + subfolder + "/" + name.getResourcePath() + ".json";
   }

   @Nullable
   public static JsonObject fromAssetsFile(ResourceLocation name, String subfolder) throws UnsupportedEncodingException, FileNotFoundException {
      return fromFile(getAssetsPath(name, subfolder));
   }

   @Nullable
   public static JsonObject fromFile(String filePath) throws UnsupportedEncodingException, FileNotFoundException {
      if (!filePath.endsWith(".json")) {
         filePath = filePath + ".json";
      }

      String resourcePath = filePath;
      if (resourcePath.startsWith("assets/")) {
         int end = resourcePath.indexOf('/', 7);
         ModContainer mod = end == -1 ? null : Loader.instance().getIndexedModList().get(resourcePath.substring(7, end));
         if (mod != null) {
            resourcePath = mod.getAssetsPrefix() + resourcePath.substring(7);
         }
      }

      InputStream resourceAsStream = EagRuntime.getResourceStream("/" + resourcePath);
      if (resourceAsStream != null) {
         BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"));
         JsonElement jsonElement = (JsonElement)GSON.fromJson(reader, JsonElement.class);
         return jsonElement.getAsJsonObject();
      } else {
         throw new FileNotFoundException("Could not find file " + filePath);
      }
   }
}
