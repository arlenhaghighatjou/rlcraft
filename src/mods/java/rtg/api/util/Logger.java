package rtg.api.util;

import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import rtg.RTGConfig;

public final class Logger {
   private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("rtg".toUpperCase());

   private Logger() {
   }

   public static void rtgDebug(String format, Object... data) {
      if (RTGConfig.enableDebugging()) {
         LOGGER.log(Level.DEBUG, format, data);
      }

   }

   public static void debug(String format, Object... data) {
      LOGGER.log(Level.DEBUG, format, data);
   }

   public static void trace(String format, Object... data) {
      LOGGER.log(Level.TRACE, format, data);
   }

   public static void info(String format, Object... data) {
      LOGGER.log(Level.INFO, format, data);
   }

   public static void warn(String format, Object... data) {
      LOGGER.log(Level.WARN, format, data);
   }

   public static void error(String format, Object... data) {
      LOGGER.log(Level.ERROR, format, data);
   }

   public static void fatal(String format, Object... data) {
      LOGGER.log(Level.FATAL, format, data);
   }
}
