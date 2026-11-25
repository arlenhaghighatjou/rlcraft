package org.orecruncher.lib;

import javax.annotation.Nonnull;
import net.minecraft.world.World;

public class MinecraftClock {
   private static final String AM = Localization.format("orelib.format.AM");
   private static final String PM = Localization.format("orelib.format.PM");
   private static final String NO_SKY = Localization.format("orelib.format.NoSky");
   private static final String SUNRISE = Localization.format("orelib.format.Sunrise");
   private static final String SUNSET = Localization.format("orelib.format.Sunset");
   private static final String DAYTIME = Localization.format("orelib.format.Daytime");
   private static final String NIGHTTIME = Localization.format("orelib.format.Nighttime");
   private static final String TIME_FORMAT = Localization.loadString("orelib.format.TimeOfDay");
   protected int day;
   protected int hour;
   protected int minute;
   protected boolean isAM;
   protected DiurnalUtils.DayCycle cycle = DiurnalUtils.DayCycle.DAYTIME;

   public MinecraftClock() {
   }

   public MinecraftClock(@Nonnull World world) {
      this.update(world);
   }

   public void update(@Nonnull World world) {
      long time = world.getWorldTime();
      this.day = (int)(time / 24000L);
      time = time - (long)(this.day * 24000);
      ++this.day;
      this.hour = (int)(time / 1000L);
      time = time - (long)(this.hour * 1000);
      this.minute = (int)((double)time / 16.666D);
      this.hour += 6;
      if (this.hour >= 24) {
         this.hour -= 24;
         ++this.day;
      }

      this.isAM = this.hour < 12;
      this.cycle = DiurnalUtils.getCycle(world);
   }

   public int getDay() {
      return this.day;
   }

   public int getHour() {
      return this.hour;
   }

   public int getMinute() {
      return this.minute;
   }

   public boolean isAM() {
      return this.isAM;
   }

   public String getTimeOfDay() {
      switch(this.cycle) {
      case NO_SKY:
         return NO_SKY;
      case SUNRISE:
         return SUNRISE;
      case SUNSET:
         return SUNSET;
      case DAYTIME:
         return DAYTIME;
      default:
         return NIGHTTIME;
      }
   }

   public String getFormattedTime() {
      return String.format(TIME_FORMAT, this.day, this.hour > 12 ? this.hour - 12 : this.hour, this.minute, this.isAM ? AM : PM);
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      return builder.append('[').append(this.getFormattedTime()).append(' ').append(this.getTimeOfDay()).append(']').toString();
   }
}
