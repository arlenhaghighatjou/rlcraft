package crafttweaker.zenscript;

import crafttweaker.CraftTweakerAPI;
import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.util.ZenPosition;

public class CrTErrorLogger implements IZenErrorLogger {
   public CrTErrorLogger() {
   }

   public void error(ZenPosition position, String message) {
      if (position == null) {
         CraftTweakerAPI.logError("system: " + message);
      } else {
         CraftTweakerAPI.logError(position + ": " + message);
      }

   }

   public void warning(ZenPosition position, String message) {
      if (position == null) {
         CraftTweakerAPI.logWarning("system: " + message);
      } else {
         CraftTweakerAPI.logWarning(position + ": " + message);
      }

   }

   public void info(ZenPosition position, String message) {
      if (position == null) {
         CraftTweakerAPI.logInfo("system: " + message);
      } else {
         CraftTweakerAPI.logInfo(position + ": " + message);
      }

   }

   public void error(String message) {
      this.error((ZenPosition)null, message);
   }

   public void error(String message, Throwable e) {
      CraftTweakerAPI.logError("system: " + message, e);
   }

   public void warning(String message) {
      this.warning((ZenPosition)null, message);
   }

   public void info(String message) {
      this.info((ZenPosition)null, message);
   }
}
