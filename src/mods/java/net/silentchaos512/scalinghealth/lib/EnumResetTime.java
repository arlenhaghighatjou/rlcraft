package net.silentchaos512.scalinghealth.lib;

import java.util.Calendar;
import javax.annotation.Nullable;
import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.scalinghealth.ScalingHealth;

public enum EnumResetTime {
   NONE,
   SUNDAY,
   MONDAY,
   TUESDAY,
   WEDNESDAY,
   THURSDAY,
   FRIDAY,
   SATURDAY,
   DAILY,
   MONTHLY;

   private EnumResetTime() {
   }

   public boolean shouldReset(Calendar today, Calendar lastTimePlayed) {
      int todayDate = today.get(5);
      int lastDate = lastTimePlayed.get(5);
      int todayMonth = today.get(2);
      int lastMonth = today.get(2);
      int todayYear = today.get(1);
      int lastYear = today.get(1);
      boolean isSameDate = todayDate == lastDate && todayMonth == lastMonth && todayYear == lastYear;
      switch(this) {
      case NONE:
         return false;
      case DAILY:
         return !isSameDate;
      case MONTHLY:
         return todayMonth != lastMonth;
      case SUNDAY:
      case MONDAY:
      case TUESDAY:
      case WEDNESDAY:
      case THURSDAY:
      case FRIDAY:
      case SATURDAY:
         Calendar nextReset = this.getNextResetDay(lastTimePlayed);
         return !today.before(nextReset);
      default:
         ScalingHealth.logHelper.warn("Unknown EnumResetTime: " + this, new Object[0]);
         return false;
      }
   }

   @Nullable
   private Calendar getNextResetDay(Calendar lastTimePlayed) {
      Calendar nextReset = Calendar.getInstance();
      nextReset.setTime(lastTimePlayed.getTime());
      nextReset.set(10, 0);
      nextReset.set(12, 1);
      nextReset.set(13, 1);
      nextReset.set(9, 0);
      nextReset.add(5, 1);
      int nextResetDayOfWeek = this.getNextResetDayOfWeek();
      if (nextResetDayOfWeek < 0) {
         return null;
      } else {
         while(nextReset.get(7) != nextResetDayOfWeek) {
            nextReset.add(5, 1);
         }

         return nextReset;
      }
   }

   private int getNextResetDayOfWeek() {
      switch(this) {
      case SUNDAY:
         return 1;
      case MONDAY:
         return 2;
      case TUESDAY:
         return 3;
      case WEDNESDAY:
         return 4;
      case THURSDAY:
         return 5;
      case FRIDAY:
         return 6;
      case SATURDAY:
         return 7;
      default:
         return -1;
      }
   }

   public static EnumResetTime loadFromConfig(Configuration c, EnumResetTime defaultValue, String category) {
      String[] validValues = new String[values().length];

      for(int i = 0; i < values().length; ++i) {
         validValues[i] = values()[i].name();
      }

      String whichOne = category.equals("main.difficulty") ? "difficulty" : "health";
      String str = c.getString("Reset Time", category, defaultValue.name(), "Allows players' " + whichOne + " to be reset at certain frequencies.\n  NONE - Do not do regular resets.  Weekdays (SUNDAY, MONDAY, etc.) - Reset on this day of the week. If the player does not play on this day, they will be reset the next time they log in.\n  DAILY - Reset if the last time the player logged in was a different day.  MONTHLY - Reset if the last time the player logged in was a different month (everyone resets on the first of the month).", validValues);

      for(EnumResetTime mode : values()) {
         if (mode.name().equalsIgnoreCase(str)) {
            return mode;
         }
      }

      return defaultValue;
   }
}
