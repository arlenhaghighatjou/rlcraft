package org.orecruncher.lib;

import org.apache.logging.log4j.LogManager;
import org.orecruncher.lib.logging.ModLog;

public final class LibLog {
   private static final ModLog logger = ModLog.setLogger("blockartistry.lib", LogManager.getLogger("blockartistry.lib"));

   private LibLog() {
   }

   public static ModLog log() {
      return logger;
   }
}
