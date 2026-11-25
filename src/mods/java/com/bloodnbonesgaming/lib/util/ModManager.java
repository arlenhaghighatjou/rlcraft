package com.bloodnbonesgaming.lib.util;

import com.bloodnbonesgaming.lib.BNBGamingMod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

public class ModManager {
   private static Map<String, BNBGamingMod> registeredMods = Maps.newLinkedHashMap();

   public ModManager() {
   }

   public static boolean registerMod(String modID, BNBGamingMod mod) {
      if (registeredMods.containsKey(modID)) {
         return false;
      } else {
         registeredMods.put(modID, mod);
         return true;
      }
   }

   public static Map<String, BNBGamingMod> getRegistredModsMap() {
      return Maps.newLinkedHashMap(registeredMods);
   }

   public static List<BNBGamingMod> getRegisteredMods() {
      return Lists.newArrayList(registeredMods.values());
   }
}
