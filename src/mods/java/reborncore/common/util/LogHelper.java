package reborncore.common.util;

import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import reborncore.common.IModInfo;

public class LogHelper {
   Logger logger;

   public LogHelper(IModInfo modInfo) {
      this.logger = LogManager.getLogger(modInfo.MOD_ID());
   }

   public void log(Level logLevel, Object object) {
      this.logger.log(logLevel, String.valueOf(object));
   }

   public void all(Object object) {
      this.log(Level.ALL, object);
   }

   public void debug(Object object) {
      this.log(Level.DEBUG, object);
   }

   public void error(Object object) {
      this.log(Level.ERROR, object);
   }

   public void fatal(Object object) {
      this.log(Level.FATAL, object);
   }

   public void info(Object object) {
      this.log(Level.INFO, object);
   }

   public void off(Object object) {
      this.log(Level.OFF, object);
   }

   public void trace(Object object) {
      this.log(Level.TRACE, object);
   }

   public void warn(Object object) {
      this.log(Level.WARN, object);
   }
}
