package ivorius.ivtoolkit.tools;

import java.util.Calendar;

public class IvDateHelper {
   public IvDateHelper() {
   }

   public static boolean isHalloween() {
      Calendar cal = Calendar.getInstance();
      return cal.get(2) == 9 && cal.get(5) == 31;
   }

   public static boolean isChristmas() {
      Calendar cal = Calendar.getInstance();
      int day = cal.get(5);
      return cal.get(2) == 11 && day == 23 || day == 24;
   }

   public static boolean isAprilFools() {
      Calendar cal = Calendar.getInstance();
      return cal.get(2) == 3 && cal.get(5) == 1;
   }

   public static boolean isMardiGras() {
      Calendar cal = Calendar.getInstance();
      return cal.get(2) == 2 && cal.get(5) == 4;
   }

   public static boolean isOppositeDay() {
      Calendar cal = Calendar.getInstance();
      return cal.get(2) == 0 && cal.get(5) == 25;
   }
}
