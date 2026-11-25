package com.teamacronymcoders.base.util.logging;

import com.teamacronymcoders.base.util.Platform;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ModLogger implements ILogger {
   private Logger modLog;
   private boolean isDev;

   public ModLogger(String modid) {
      this(LogManager.getLogger(modid));
   }

   public ModLogger(Logger modLog) {
      this.modLog = modLog;
      this.isDev = Platform.isDevEnv();
   }

   public void warning(String message) {
      this.modLog.warn(message);
   }

   public void info(String message) {
      this.modLog.info(message);
   }

   public void fatal(String message) {
      this.modLog.fatal(message);
   }

   public void error(String message) {
      this.modLog.fatal(message);
   }

   public void error(Throwable throwable) {
      this.modLog.error(throwable);
   }

   public void devWarning(String message) {
      if (this.isDev) {
         this.warning(message);
      }

   }

   public void devInfo(String message) {
      if (this.isDev) {
         this.info(message);
      }

   }

   public void devFatal(String message) {
      if (this.isDev) {
         this.info(message);
      }

   }

   public void devError(String message) {
      if (this.isDev) {
         this.error(message);
      }

   }

   public Logger getLogger() {
      return this.modLog;
   }
}
