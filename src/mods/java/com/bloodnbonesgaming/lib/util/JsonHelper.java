package com.bloodnbonesgaming.lib.util;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

public class JsonHelper {
   private static final JsonParser parser = new JsonParser();
   private static final Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

   public JsonHelper() {
   }

   public static JsonElement readJson(VFile2 file) {
      String text = file.getAllChars();
      if (text == null) {
         BNBGamingLib.libInstance.getLog().error("Failed to read Json file " + file.getName());
         return null;
      }

      return parser.parse(text);
   }

   public static boolean writeJson(JsonElement e, VFile2 file) {
      return writeJson(e, file, gson);
   }

   public static boolean writeJson(JsonElement e, VFile2 file, Gson gson) {
      file.setAllChars(gson.toJson(e));
      return true;
   }

   public static JsonElement toJsonTree(Object obj) {
      return toJsonTree(obj, gson);
   }

   public static JsonElement toJsonTree(Object obj, Gson gson) {
      return gson.toJsonTree(obj);
   }

   public static boolean writeGson(Object obj, VFile2 file) {
      return writeGson(obj, file, gson);
   }

   public static boolean writeGson(Object obj, VFile2 file, Gson gson) {
      file.setAllChars(gson.toJson(obj));
      return true;
   }

   public static <T> T readGson(Class<T> clazz, VFile2 file) {
      return readGson(clazz, file, gson);
   }

   public static <T> T readGson(Class<T> clazz, VFile2 file, Gson gson) {
      String text = file.getAllChars();
      if (text == null) {
         BNBGamingLib.libInstance.getLog().error("Failed to read Json from file " + file.getName());
         return null;
      }

      return gson.fromJson(text, clazz);
   }

   public static <T> T readGson(Type type, VFile2 file) {
      return readGson(type, file, gson);
   }

   public static <T> T readGson(Type type, VFile2 file, Gson gson) {
      String text = file.getAllChars();
      if (text == null) {
         BNBGamingLib.libInstance.getLog().error("Failed to read Json from file " + file.getName());
         return null;
      }

      return gson.fromJson(text, type);
   }
}
