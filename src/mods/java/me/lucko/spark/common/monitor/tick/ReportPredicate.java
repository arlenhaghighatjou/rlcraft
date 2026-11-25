package me.lucko.spark.common.monitor.tick;

import me.lucko.spark.lib.adventure.text.Component;

public interface ReportPredicate {
   boolean shouldReport(double var1, double var3, double var5);

   Component monitoringStartMessage();

   public static final class DurationGt implements ReportPredicate {
      private final double threshold;

      public DurationGt(double threshold) {
         this.threshold = threshold;
      }

      public boolean shouldReport(double duration, double increaseFromAvg, double percentageChange) {
         if (increaseFromAvg <= 0.0D) {
            return false;
         } else {
            return duration > this.threshold;
         }
      }

      public Component monitoringStartMessage() {
         return Component.text("Starting now, any ticks with duration >" + this.threshold + " will be reported.");
      }
   }

   public static final class PercentageChangeGt implements ReportPredicate {
      private final double threshold;

      public PercentageChangeGt(double threshold) {
         this.threshold = threshold;
      }

      public boolean shouldReport(double duration, double increaseFromAvg, double percentageChange) {
         if (increaseFromAvg <= 0.0D) {
            return false;
         } else {
            return percentageChange > this.threshold;
         }
      }

      public Component monitoringStartMessage() {
         return Component.text("Starting now, any ticks with >" + this.threshold + "% increase in duration compared to the average will be reported.");
      }
   }
}
