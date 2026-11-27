package mezz.jei.util;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class LimitedLogger {
   private final Map<String, Long> logTimes = new HashMap();
   private final Logger logger;
   private final long timeBetweenLoggingMs;

   public LimitedLogger(Logger logger, long timeBetweenLoggingMs) {
      this.logger = logger;
      this.timeBetweenLoggingMs = timeBetweenLoggingMs;
   }

   public void log(Level level, String key, String message, Object... params) {
      if (level.levelInt >= LogManager.logLevel.levelInt) {
         long now = System.currentTimeMillis();
         Long lastTime = (Long)this.logTimes.get(key);
         if (lastTime == null || now - lastTime.longValue() > this.timeBetweenLoggingMs) {
            this.logTimes.put(key, Long.valueOf(now));
            this.logger.log(level, message, params);
         }
      }

   }
}
