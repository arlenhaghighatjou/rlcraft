package crafttweaker.runtime;

import crafttweaker.CraftTweakerAPI;

public class GlobalFunctions {
   private GlobalFunctions() {
   }

   public static void print(String message) {
      CraftTweakerAPI.logInfo(message);
   }

   public static int totalActions() {
      return CraftTweakerAPI.tweaker.getActions().size();
   }

   public static void enableDebug() {
      CraftTweakerAPI.tweaker.enableDebug();
   }

   public static boolean isNull(Object any) {
      return any == null;
   }
}
