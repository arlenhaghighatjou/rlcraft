package me.lucko.spark.common.util;

public enum FormatUtil {
   private static final String[] SIZE_UNITS = new String[]{"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

   private FormatUtil() {
   }

   public static String percent(double value, double max) {
      double percent = value * 100.0D / max;
      return (int)percent + "%";
   }

   public static String formatBytes(long bytes) {
      if (bytes == 0L) {
         return "0 bytes";
      } else {
         int sizeIndex = (int)(Math.log((double)bytes) / Math.log(1024.0D));
         return String.format("%.1f", (double)bytes / Math.pow(1024.0D, (double)sizeIndex)) + " " + SIZE_UNITS[sizeIndex];
      }
   }
}
