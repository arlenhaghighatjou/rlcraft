package me.lucko.spark.api.statistic.misc;

public interface DoubleAverageInfo {
   double mean();

   double max();

   double min();

   default double median() {
      return this.percentile(0.5D);
   }

   default double percentile95th() {
      return this.percentile(0.95D);
   }

   double percentile(double var1);
}
