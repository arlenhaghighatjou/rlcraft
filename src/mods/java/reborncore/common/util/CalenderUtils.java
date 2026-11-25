package reborncore.common.util;

import java.util.Calendar;
import reborncore.RebornCore;

public class CalenderUtils {
   public static boolean christmas;

   public CalenderUtils() {
   }

   public static void loadCalender() {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      int day = calendar.get(5);
      int month = calendar.get(2) + 1;
      if (month == 12 && day >= 24 && day <= 26) {
         christmas = true;
         RebornCore.logHelper.info("Merry christmas from reborn core! :)");
      }

   }
}
