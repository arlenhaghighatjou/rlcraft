package me.ichun.mods.ichunutil.common.core.util;

import java.util.Calendar;

public class EventCalendar {
   private static boolean isNewYear;
   private static boolean isValentinesDay;
   private static boolean isPgBirthday;
   private static boolean isAFDay;
   private static boolean isHalloween;
   private static boolean isChristmas;
   public static int day;

   public EventCalendar() {
   }

   public static void checkDate() {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      day = calendar.get(5);
      switch(calendar.get(2)) {
      case 0:
         if (day == 1) {
            isNewYear = true;
         }
         break;
      case 1:
         if (day == 14) {
            isValentinesDay = true;
         }
      case 2:
         if (day == 9) {
            isPgBirthday = true;
         }
         break;
      case 3:
         if (day == 1) {
            isAFDay = true;
         }
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 10:
      default:
         break;
      case 9:
         if (day == 31) {
            isHalloween = true;
         }
         break;
      case 11:
         if (day == 25) {
            isChristmas = true;
         }
      }

   }

   public static boolean isNewYear() {
      return isNewYear;
   }

   public static boolean isValentinesDay() {
      return isValentinesDay;
   }

   public static boolean isPgBirthday() {
      return isPgBirthday;
   }

   public static boolean isAFDay() {
      return isAFDay;
   }

   public static boolean isHalloween() {
      return isHalloween;
   }

   public static boolean isChristmas() {
      return isChristmas;
   }
}
