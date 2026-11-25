package com.ferreusveritas.dynamictrees.util;

import com.ferreusveritas.dynamictrees.blocks.LeavesPaging;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.ResourceLocation;

public class JsonHelper {
   public JsonHelper() {
   }

   public static JsonElement load(ResourceLocation jsonLocation) {
      String filename = "assets/" + jsonLocation.getResourceDomain() + "/" + jsonLocation.getResourcePath();
      InputStream in = (new LeavesPaging()).getClass().getClassLoader().getResourceAsStream(filename);
      if (in == null) {
         Logger.getLogger("dynamictrees").log(Level.SEVERE, "Could not open resource " + filename);
         return null;
      } else {
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         return (JsonElement)(new Gson()).fromJson(reader, JsonElement.class);
      }
   }

   public static JsonElement load(File file) {
      if (file != null && file.exists() && file.isFile() && file.canRead()) {
         String fileName = file.getAbsolutePath();

         try {
            JsonParser parser = new JsonParser();
            return parser.parse(new FileReader(file));
         } catch (Exception var3) {
            System.err.println("Can't open " + fileName + ": " + var3.getMessage());
         }
      }

      return null;
   }
}
