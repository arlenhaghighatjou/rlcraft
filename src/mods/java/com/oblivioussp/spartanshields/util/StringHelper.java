package com.oblivioussp.spartanshields.util;

import net.minecraft.util.text.translation.I18n;

public class StringHelper {
   public StringHelper() {
   }

   public static String getItemUnlocalizedName(String locName) {
      return getItemUnlocalizedName(locName, "spartanshields");
   }

   public static String getItemUnlocalizedName(String locName, String modId) {
      return String.format("item.%s:%s", modId.toLowerCase(), stripUnlocalizedName(locName));
   }

   public static String getBlockUnlocalizedName(String locName) {
      return getBlockUnlocalizedName(locName, "spartanshields");
   }

   public static String getBlockUnlocalizedName(String locName, String modId) {
      return String.format("tile.%s:%s", modId.toLowerCase(), stripUnlocalizedName(locName));
   }

   public static String stripUnlocalizedName(String unlocalizedName) {
      return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
   }

   public static String translateString(String unlocalizedStr, String type) {
      return translateString(unlocalizedStr, type, "spartanshields");
   }

   public static String translateString(String unlocalizedStr, String type, String modIdStr) {
      String modId = modIdStr;
      if (modIdStr == null || modIdStr == "") {
         modId = "spartanshields";
      }

      return type != null && type != "" ? I18n.translateToLocalFormatted(String.format("%s.%s:%s", type, modId.toLowerCase(), unlocalizedStr), new Object[0]) : I18n.translateToLocalFormatted(modId.toLowerCase() + ":" + unlocalizedStr, new Object[0]);
   }

   public static String translateFormattedString(String unlocalizedStr, String type, Object... parameters) {
      return translateFormattedString(unlocalizedStr, type, "spartanshields", parameters);
   }

   public static String translateFormattedString(String unlocalizedStr, String type, String modIdStr, Object... parameters) {
      String modId = modIdStr;
      if (modIdStr == null || modIdStr == "") {
         modId = "spartanshields";
      }

      return type != null && type != "" ? I18n.translateToLocalFormatted(String.format("%s.%s:%s", type, modId.toLowerCase(), unlocalizedStr), parameters) : I18n.translateToLocalFormatted(modId.toLowerCase() + ":" + unlocalizedStr, parameters);
   }

   public static boolean hasTranslateKey(String key, String type) {
      return hasTranslateKey(key, type, "spartanshields");
   }

   public static boolean hasTranslateKey(String key, String type, String modId) {
      return type != null && type != "" ? I18n.canTranslate(String.format("%s.%s:%s", type, modId.toLowerCase(), key)) : I18n.canTranslate("spartanshields".toLowerCase() + ":" + key);
   }
}
