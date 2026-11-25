package org.orecruncher.lib;

import javax.annotation.Nonnull;
import net.minecraft.world.World;
import org.orecruncher.lib.math.MathStuff;

public final class DiurnalUtils {
   private DiurnalUtils() {
   }

   public static boolean isDaytime(@Nonnull World world) {
      return getCycle(world) == DiurnalUtils.DayCycle.DAYTIME;
   }

   public static boolean isNighttime(@Nonnull World world) {
      return getCycle(world) == DiurnalUtils.DayCycle.NIGHTTIME;
   }

   public static boolean isSunrise(@Nonnull World world) {
      return getCycle(world) == DiurnalUtils.DayCycle.SUNRISE;
   }

   public static boolean isSunset(@Nonnull World world) {
      return getCycle(world) == DiurnalUtils.DayCycle.SUNSET;
   }

   public static DiurnalUtils.DayCycle getCycle(@Nonnull World world) {
      if (world != null && world.provider != null && !world.provider.isNether() && world.provider.hasSkyLight()) {
         float brFactor = world.provider.getSunBrightnessFactor(1.0F);
         if (brFactor > 0.6F) {
            return DiurnalUtils.DayCycle.DAYTIME;
         } else if (brFactor < 0.1F) {
            return DiurnalUtils.DayCycle.NIGHTTIME;
         } else {
            return (double)MathStuff.sin(world.getCelestialAngleRadians(1.0F)) > 0.0D ? DiurnalUtils.DayCycle.SUNSET : DiurnalUtils.DayCycle.SUNRISE;
         }
      } else {
         return DiurnalUtils.DayCycle.NO_SKY;
      }
   }

   public static float getMoonPhaseFactor(@Nonnull World world) {
      return world == null ? 0.0F : world.getCurrentMoonPhaseFactor();
   }

   public static boolean isAuroraVisible(@Nonnull World world) {
      return !isAuroraInvisible(world);
   }

   public static boolean isAuroraInvisible(@Nonnull World world) {
      DiurnalUtils.DayCycle cycle = getCycle(world);
      return cycle == DiurnalUtils.DayCycle.SUNRISE || cycle == DiurnalUtils.DayCycle.DAYTIME;
   }

   public static enum DayCycle {
      NO_SKY,
      SUNRISE,
      SUNSET,
      DAYTIME,
      NIGHTTIME;

      private DayCycle() {
      }
   }
}
