package com.fantasticsource.tools;

import com.fantasticsource.tools.datastructures.Pair;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Benchmark {
   public Benchmark() {
   }

   public static void benchmark(int iterations, Pair<String, Runnable>... labeledEntries) {
      long totalTime = 0L;
      int i2 = 0;
      long[] times = new long[labeledEntries.length];

      for(Pair<String, Runnable> entry : labeledEntries) {
         int i = 0;

         long iTime;
         for(iTime = System.nanoTime(); i < iterations; ++i) {
            ((Runnable)entry.getValue()).run();
         }

         iTime = System.nanoTime() - iTime;
         times[i2++] = iTime;
         totalTime += iTime;
      }

      NumberFormat timeFormat = NumberFormat.getInstance();
      NumberFormat percentFormat = DecimalFormat.getInstance();
      i2 = 0;

      for(long time : times) {
         String label = (String)labeledEntries[i2++].getKey() + ": ";
         String timeString = timeFormat.format(time) + " nanos";
         String percent = percentFormat.format((double)time / (double)totalTime * 100.0D);
         System.out.println(label + timeString + " (" + percent + "%)");
      }

   }
}
