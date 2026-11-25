package net.silentchaos512.lib.util;

public final class TimeHelper {
   public static final int TICKS_PER_SECOND = 20;
   public static final int VANILLA_DAY_LENGTH = 24000;

   private TimeHelper() {
      throw new IllegalAccessError("Utility class");
   }

   public static int ticksFromSeconds(float seconds) {
      return (int)(20.0F * seconds);
   }

   public static int ticksFromMinutes(float minutes) {
      return (int)(1200.0F * minutes);
   }

   public static int ticksFromHours(float hours) {
      return (int)(72000.0F * hours);
   }
}
