package net.silentchaos512.lib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.text.WordUtils;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class LogHelper {
   private static final Map<String, LogHelper> LOGGER_BY_MODNAME = new HashMap();
   private final Logger logger;
   private final int buildNumber;
   private String lastDebugOutput = "";

   public LogHelper(String modName, int buildNumber) {
      this.logger = LogManager.getLogger(modName);
      this.buildNumber = buildNumber;
      this.addLogHelper(modName);
   }

   public LogHelper(Logger providedLogger, int buildNumber) {
      this.logger = providedLogger;
      this.buildNumber = buildNumber;
      this.addLogHelper(providedLogger.getName());
   }

   private void addLogHelper(String modName) {
      LOGGER_BY_MODNAME.put(modName, this);
      this.debug("Add LogHelper for \"{}\"", modName);
   }

   public static Optional<LogHelper> getRegisteredLogger(String modName) {
      return !LOGGER_BY_MODNAME.containsKey(modName) ? Optional.empty() : Optional.of(LOGGER_BY_MODNAME.get(modName));
   }

   public void catching(Throwable t) {
      this.logger.catching(t);
   }

   public void debug(String msg, Object... params) {
      this.logger.debug(msg, params);
      if (this.buildNumber == 0) {
         String newOutput = this.logger.getMessageFactory().newMessage(msg, params).getFormattedMessage();
         if (!newOutput.equals(this.lastDebugOutput)) {
            this.info("[DEBUG] " + newOutput);
            this.lastDebugOutput = newOutput;
         }
      }

   }

   public void error(String msg, Object... params) {
      this.logger.error(msg, params);
   }

   public void fatal(String msg, Object... params) {
      this.logger.fatal(msg, params);
   }

   public void info(String msg, Object... params) {
      this.logger.info(msg, params);
   }

   public void log(Level level, String msg, Object... params) {
      this.logger.log(level, msg, params);
   }

   public void trace(String msg, Object... params) {
      this.logger.trace(msg, params);
   }

   public void warn(String msg, Object... params) {
      this.logger.warn(msg, params);
   }

   public void warn(Throwable t, String msg, Object... params) {
      this.logger.warn(msg, params);
      this.logger.catching(t);
   }

   public void noticableWarning(boolean trace, List<String> lines) {
      this.error("********************************************************************************");

      for(String line : lines) {
         for(String subline : wrapString(line, 78, false, new ArrayList())) {
            this.error("* " + subline);
         }
      }

      if (trace) {
         StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

         for(int i = 2; i < 8 && i < stackTrace.length; ++i) {
            this.warn("*  at {}{}", stackTrace[i].toString(), i == 7 ? "..." : "");
         }
      }

      this.error("********************************************************************************");
   }

   private static List<String> wrapString(String string, int lnLength, boolean wrapLongWords, List<String> list) {
      String[] lines = WordUtils.wrap(string, lnLength, (String)null, wrapLongWords).split(SystemUtils.LINE_SEPARATOR);
      Collections.addAll(list, lines);
      return list;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public int getBuildNumber() {
      return this.buildNumber;
   }
}
