package com.charles445.rltweaker.asm.util;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ASMLogger {
   public static Logger logger = LogManager.getLogger("RLTweakerASM");

   public ASMLogger() {
   }

   public static void info(String message) {
      logger.info(message);
   }

   public static void warn(String message) {
      logger.warn(message);
   }

   public static void error(String message) {
      logger.error(message);
   }

   public static void error(String message, Throwable t) {
      logger.error(message, t);
   }
}
