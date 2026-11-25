package com.charles445.simpledifficulty.api;

import java.util.ArrayList;
import java.util.List;

public class SDCompatibility {
   public static boolean defaultThirstDisplay = true;
   public static List<String> disabledDefaultJson = new ArrayList();
   public static List<String> disabledCompletely = new ArrayList();

   public SDCompatibility() {
   }

   public static void disableBuiltInModJsonConfiguration(String modid) {
      disabledDefaultJson.add(modid);
   }

   public static void disableBuiltInModCompatibility(String modid) {
      disabledDefaultJson.add(modid);
      disabledCompletely.add(modid);
   }
}
