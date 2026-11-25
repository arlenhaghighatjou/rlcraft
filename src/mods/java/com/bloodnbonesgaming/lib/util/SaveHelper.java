package com.bloodnbonesgaming.lib.util;

import com.google.gson.Gson;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

public class SaveHelper {
   private static VFile2 serverSaveFolder;

   public SaveHelper() {
   }

   public static void setServerSaveFolder(VFile2 saveFolder) {
      serverSaveFolder = saveFolder;
   }

   private static VFile2 playerFile(EaglercraftUUID playerUUID, String subFolder) {
      return subFolder != null ? new VFile2(serverSaveFolder, subFolder, playerUUID.toString() + ".json") : new VFile2(serverSaveFolder, playerUUID.toString() + ".json");
   }

   public static void saveDataForPlayer(EaglercraftUUID playerUUID, String subFolder, Object objToSave) {
      JsonHelper.writeGson(objToSave, playerFile(playerUUID, subFolder));
   }

   public static void saveDataForPlayer(EaglercraftUUID playerUUID, String subFolder, Object objToSave, Gson gson) {
      JsonHelper.writeGson(objToSave, playerFile(playerUUID, subFolder), gson);
   }

   public static <T> T loadDataForPlayer(EaglercraftUUID playerUUID, String subFolder, Class<T> clazz) {
      return JsonHelper.readGson(clazz, playerFile(playerUUID, subFolder));
   }

   public static <T> T loadDataForPlayer(EaglercraftUUID playerUUID, String subFolder, Class<T> clazz, Gson gson) {
      return JsonHelper.readGson(clazz, playerFile(playerUUID, subFolder), gson);
   }
}
