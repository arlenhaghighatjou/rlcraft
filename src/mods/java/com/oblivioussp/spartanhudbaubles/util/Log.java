package com.oblivioussp.spartanhudbaubles.util;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class Log {
   protected static final Logger LOGGER = LogManager.getLogger("Spartan HUD: Baubles");

   public Log() {
   }

   public static void debug(Object object) {
      LOGGER.debug(String.valueOf(object));
   }

   public static void error(Object object) {
      LOGGER.error(String.valueOf(object));
   }

   public static void fatal(Object object) {
      LOGGER.fatal(String.valueOf(object));
   }

   public static void info(Object object) {
      LOGGER.info(String.valueOf(object));
   }

   public static void trace(Object object) {
      LOGGER.trace(String.valueOf(object));
   }

   public static void warn(Object object) {
      LOGGER.warn(String.valueOf(object));
   }
}
