package crafttweaker.runtime;

import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

public interface ILogger {
   @ZenMethod
   void logCommand(String var1);

   @ZenMethod
   void logInfo(String var1);

   @ZenMethod
   void logWarning(String var1);

   @ZenMethod
   void logError(String var1);

   @ZenMethod
   void logError(String var1, Throwable var2);

   @ZenMethod
   void logPlayer(IPlayer var1);

   @ZenMethod
   void logDefault(String var1);

   @ZenGetter("logDisabled")
   boolean isLogDisabled();

   @ZenSetter("logDisabled")
   void setLogDisabled(boolean var1);
}
