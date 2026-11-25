package com.blamejared.compat.botania;

import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;

public class LocalizationHelper {
   public LocalizationHelper() {
   }

   public static void setLocale(String unlocalized, String localized) {
      try {
         Field locale = I18n.class.getDeclaredField("i18nLocale");
         locale.setAccessible(true);
         Locale loc = (Locale)locale.get((Object)null);
         Field propMap = loc.getClass().getDeclaredField("properties");
         propMap.setAccessible(true);
         Map<String, String> map = (Map)propMap.get(loc);
         map.put(unlocalized, localized);
      } catch (Exception var6) {
         ;
      }

   }
}
