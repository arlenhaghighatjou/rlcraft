package com.oblivioussp.spartanweaponry.util;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class Log {
   protected static final Logger LOGGER = LogManager.getLogger("Spartan Weaponry");

   public Log() {
   }

   public static void debug(Object object) {
      LOGGER.debug(object);
   }

   public static void error(Object object) {
      LOGGER.error(object);
   }

   public static void fatal(Object object) {
      LOGGER.fatal(object);
   }

   public static void info(Object object) {
      LOGGER.info(object);
   }

   public static void trace(Object object) {
      LOGGER.trace(object);
   }

   public static void warn(Object object) {
      LOGGER.warn(object);
   }
}
