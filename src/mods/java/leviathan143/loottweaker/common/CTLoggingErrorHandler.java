package leviathan143.loottweaker.common;

import crafttweaker.CraftTweakerAPI;

public class CTLoggingErrorHandler implements ErrorHandler {
   public CTLoggingErrorHandler() {
   }

   public void error(String format, Object... args) {
      CraftTweakerAPI.logError(String.format(format, args));
   }

   public void error(String message) {
      CraftTweakerAPI.logError(message);
   }

   public void warn(String format, Object... args) {
      CraftTweakerAPI.logWarning(String.format(format, args));
   }

   public void warn(String message) {
      CraftTweakerAPI.logWarning(message);
   }
}
