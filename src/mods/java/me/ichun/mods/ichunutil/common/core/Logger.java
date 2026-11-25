package me.ichun.mods.ichunutil.common.core;

import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;

public class Logger {
   public final String modName;
   public final org.apache.logging.log4j.Logger logger;

   private Logger(String id) {
      this.modName = id;
      this.logger = LogManager.getLogger(this.modName);
   }

   public void log(Level logLevel, String format, Object... msg) {
      this.logger.log(logLevel, String.format(format, msg));
   }

   public void log(Level logLevel, Object msg) {
      this.log(logLevel, "%s", msg);
   }

   public void warn(Object msg) {
      this.log(Level.WARN, msg);
   }

   public void info(Object msg) {
      this.log(Level.INFO, msg);
   }

   public void error(Object msg) {
      this.log(Level.ERROR, msg);
   }

   public static Logger createLogger(String modId) {
      return new Logger(modId);
   }
}
