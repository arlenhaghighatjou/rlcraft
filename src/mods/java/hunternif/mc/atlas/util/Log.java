package hunternif.mc.atlas.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class Log {
   private static final Logger LOGGER = LogManager.getLogger("antiqueatlas");

   public Log() {
   }

   private static void log(Level level, Throwable ex, String msg, Object... data) {
      LOGGER.log(level, () -> {
         return data == null ? msg : String.format(msg, data);
      }, ex);
   }

   public static void debug(String msg, Object... data) {
      log(Level.DEBUG, (Throwable)null, msg, data);
   }

   public static void info(String msg, Object... data) {
      log(Level.INFO, (Throwable)null, msg, data);
   }

   public static void warn(String msg, Object... data) {
      log(Level.WARN, (Throwable)null, msg, data);
   }

   public static void warn(Throwable ex, String msg, Object... data) {
      log(Level.WARN, ex, msg, data);
   }

   public static void error(String msg, Object... data) {
      log(Level.ERROR, (Throwable)null, msg, data);
   }

   public static void error(Throwable ex, String msg, Object... data) {
      log(Level.ERROR, ex, msg, data);
   }

   public static void fatal(String msg, Object... data) {
      log(Level.FATAL, (Throwable)null, msg, data);
   }

   public static void fatal(Throwable ex, String msg, Object... data) {
      log(Level.FATAL, ex, msg, data);
   }
}
