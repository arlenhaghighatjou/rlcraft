package org.orecruncher.lib;

import java.time.Instant;

public class TimeUtils {
   private TimeUtils() {
   }

   public static long currentTimeMillis() {
      return System.nanoTime() / 1000000L;
   }

   public static long getGMTDaySeedBase() {
      long days = Instant.now().toEpochMilli() / 1440000L;
      return days * 311L;
   }
}
