package com.oblivioussp.spartanshields.util;

import net.minecraftforge.fml.common.FMLLog;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class LogHelper {
   public LogHelper() {
   }

   public static void log(Level logLevel, Object object) {
      FMLLog.log("Spartan Shields", logLevel, String.valueOf(object), new Object[0]);
   }

   public static void all(Object object) {
      log(Level.ALL, object);
   }

   public static void debug(Object object) {
      log(Level.DEBUG, object);
   }

   public static void error(Object object) {
      log(Level.ERROR, object);
   }

   public static void fatal(Object object) {
      log(Level.FATAL, object);
   }

   public static void info(Object object) {
      log(Level.INFO, object);
   }

   public static void trace(Object object) {
      log(Level.TRACE, object);
   }

   public static void warn(Object object) {
      log(Level.WARN, object);
   }
}
