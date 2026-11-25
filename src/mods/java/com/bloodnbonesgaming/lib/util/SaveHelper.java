package com.bloodnbonesgaming.lib.util;

import com.google.gson.Gson;
import java.io.File;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;

public class SaveHelper {
   private static File serverSaveFolder;

   public SaveHelper() {
   }

   public static void setServerSaveFolder(File saveFolder) {
      serverSaveFolder = saveFolder;
   }

   public static void saveDataForPlayer(EaglercraftUUID playerUUID, File subFile, Object objToSave) {
      File directory = subFile != null ? new File(serverSaveFolder, subFile.getPath()) : serverSaveFolder;
      directory.mkdirs();
      File file = new File(directory, playerUUID.toString() + ".json");
      JsonHelper.writeGson(objToSave, file);
   }

   public static void saveDataForPlayer(EaglercraftUUID playerUUID, File subFile, Object objToSave, Gson gson) {
      File directory = subFile != null ? new File(serverSaveFolder, subFile.getPath()) : serverSaveFolder;
      directory.mkdirs();
      File file = new File(directory, playerUUID.toString() + ".json");
      JsonHelper.writeGson(objToSave, file, gson);
   }

   public static <T> T loadDataForPlayer(EaglercraftUUID playerUUID, File subFile, Class<T> clazz) {
      File directory = subFile != null ? new File(serverSaveFolder, subFile.getPath()) : serverSaveFolder;
      directory.mkdirs();
      File file = new File(directory, playerUUID.toString() + ".json");
      return (T)JsonHelper.readGson(clazz, file);
   }

   public static <T> T loadDataForPlayer(EaglercraftUUID playerUUID, File subFile, Class<T> clazz, Gson gson) {
      File directory = subFile != null ? new File(serverSaveFolder, subFile.getPath()) : serverSaveFolder;
      directory.mkdirs();
      File file = new File(directory, playerUUID.toString() + ".json");
      return (T)JsonHelper.readGson(clazz, file, gson);
   }
}
