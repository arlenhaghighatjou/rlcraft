package com.ferreusveritas.dynamictrees.util;

import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Loader;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.ResourceLocation;

public class JsonHelper {
   public JsonHelper() {
   }

   public static JsonElement load(ResourceLocation jsonLocation) {
      ModContainer mod = Loader.instance().getIndexedModList().get(jsonLocation.getResourceDomain());
      String filename = (mod != null ? mod.getAssetsPrefix() : "assets/") + jsonLocation.getResourceDomain() + "/" + jsonLocation.getResourcePath();
      InputStream in = EagRuntime.getResourceStream("/" + filename);
      if (in == null) {
         Logger.getLogger("dynamictrees").log(Level.SEVERE, "Could not open resource " + filename);
         return null;
      } else {
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         return (JsonElement)(new Gson()).fromJson(reader, JsonElement.class);
      }
   }

   public static JsonElement load(VFile2 file) {
      if (file != null && file.exists()) {
         String fileName = file.getPath();

         try {
            JsonParser parser = new JsonParser();
            return parser.parse(file.getAllChars());
         } catch (Exception var3) {
            System.err.println("Can't open " + fileName + ": " + var3.getMessage());
         }
      }

      return null;
   }
}
