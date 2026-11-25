package com.endertech.minecraft.forge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeLog {
   private final boolean inDev = System.getProperty("INDEV") != null;
   public final Logger LOGGER;

   public ForgeLog(String modId) {
      this.LOGGER = LogManager.getLogger(modId);
   }

   public void warn(String msg) {
      this.LOGGER.warn(msg);
   }

   public void error(String msg) {
      this.LOGGER.error(msg);
   }

   public void info(String msg) {
      if (this.inDev) {
         this.LOGGER.info(msg);
      } else {
         this.LOGGER.debug(msg);
      }

   }

   public void debug(String msg) {
      if (this.inDev) {
         this.LOGGER.info("INDEV: " + msg);
      } else {
         this.LOGGER.debug(msg);
      }

   }
}
