package net.theawesomegem.fishingmadebetter.util;

public class TimeUtil {
   public TimeUtil() {
   }

   public static long minutesToMinecraftTicks(int minutes) {
      return secondsToMinecraftTicks(minutes * 60);
   }

   public static long secondsToMinecraftTicks(int seconds) {
      return (long)(seconds * 20);
   }
}
