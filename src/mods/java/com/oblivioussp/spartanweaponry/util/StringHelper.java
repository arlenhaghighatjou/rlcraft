package com.oblivioussp.spartanweaponry.util;

import net.lax1dude.eaglercraft.v1_8.HString;
import net.minecraft.util.text.translation.I18n;

public class StringHelper {
   public StringHelper() {
   }

   public static String getItemUnlocalizedName(String locName) {
      return getItemUnlocalizedName(locName, "spartanweaponry");
   }

   public static String getItemUnlocalizedName(String locName, String modId) {
      return HString.format("item.%s:%s", modId.toLowerCase(), stripUnlocalizedName(locName));
   }

   public static String getBlockUnlocalizedName(String locName) {
      return getBlockUnlocalizedName(locName, "spartanweaponry");
   }

   public static String getBlockUnlocalizedName(String locName, String modId) {
      return HString.format("tile.%s:%s", modId.toLowerCase(), stripUnlocalizedName(locName));
   }

   public static String stripUnlocalizedName(String unlocalizedName) {
      return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
   }

   public static String translateString(String unlocalizedStr, String type) {
      return translateString(unlocalizedStr, type, "spartanweaponry");
   }

   public static String translateString(String unlocalizedStr, String type, String modIdStr) {
      String modId = modIdStr;
      if (modIdStr == null || modIdStr == "") {
         modId = "spartanweaponry";
      }

      return type != null && type != "" ? I18n.translateToLocalFormatted(HString.format("%s.%s:%s", type, modId.toLowerCase(), unlocalizedStr), new Object[0]) : I18n.translateToLocalFormatted(modId.toLowerCase() + ":" + unlocalizedStr, new Object[0]);
   }

   public static String translateFormattedString(String unlocalizedStr, String type, Object... parameters) {
      return translateFormattedString(unlocalizedStr, type, "spartanweaponry", parameters);
   }

   public static String translateFormattedString(String unlocalizedStr, String type, String modIdStr, Object... parameters) {
      String modId = modIdStr;
      if (modIdStr == null || modIdStr == "") {
         modId = "spartanweaponry";
      }

      return type != null && type != "" ? I18n.translateToLocalFormatted(HString.format("%s.%s:%s", type, modId.toLowerCase(), unlocalizedStr), parameters) : I18n.translateToLocalFormatted(modId.toLowerCase() + ":" + unlocalizedStr, parameters);
   }

   public static boolean hasTranslateKey(String key, String type) {
      return hasTranslateKey(key, type, "spartanweaponry");
   }

   public static boolean hasTranslateKey(String key, String type, String modId) {
      return type != null && type != "" ? I18n.canTranslate(HString.format("%s.%s:%s", type, modId.toLowerCase(), key)) : I18n.canTranslate("spartanweaponry".toLowerCase() + ":" + key);
   }
}
