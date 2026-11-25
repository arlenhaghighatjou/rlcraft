package mezz.jei.util;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public final class Log {
   private static final Logger LOGGER = LogManager.getLogger("jei");

   public static Logger get() {
      return LOGGER;
   }

   private Log() {
   }
}
