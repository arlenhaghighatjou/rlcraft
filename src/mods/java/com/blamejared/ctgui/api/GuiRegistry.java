package com.blamejared.ctgui.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Map;

public class GuiRegistry {
   public static BiMap<String, Integer> guiMap = HashBiMap.create();

   public GuiRegistry() {
   }

   public static void registerGui(String name) {
      registerGui(name, guiMap.size() - 1);
   }

   public static void registerGuis(String... names) {
      for(String s : names) {
         registerGui(s, guiMap.size() - 1);
      }

   }

   public static void registerGui(String name, int ID) {
      guiMap.put(name, Integer.valueOf(ID));
   }

   public static int getID(String name) {
      return ((Integer)guiMap.getOrDefault(name, Integer.valueOf(Integer.MIN_VALUE))).intValue();
   }

   public static String getName(int id) {
      return (String)guiMap.inverse().get(Integer.valueOf(id));
   }

   public static Map<String, Integer> getGuiMap() {
      return guiMap;
   }
}
