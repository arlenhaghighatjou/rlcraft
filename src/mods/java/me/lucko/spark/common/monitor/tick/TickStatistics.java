package me.lucko.spark.common.monitor.tick;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.common.util.RollingAverage;

public class TickStatistics implements TickHook.Callback, TickReporter.Callback {
   private static final long SEC_IN_NANO = TimeUnit.SECONDS.toNanos(1L);
   private static final int TPS = 20;
   private static final int TPS_SAMPLE_INTERVAL = 20;
   private static final BigDecimal TPS_BASE = (new BigDecimal(SEC_IN_NANO)).multiply(new BigDecimal(20));
   private final TickStatistics.TpsRollingAverage tps5Sec = new TickStatistics.TpsRollingAverage(5);
   private final TickStatistics.TpsRollingAverage tps10Sec = new TickStatistics.TpsRollingAverage(10);
   private final TickStatistics.TpsRollingAverage tps1Min = new TickStatistics.TpsRollingAverage(60);
   private final TickStatistics.TpsRollingAverage tps5Min = new TickStatistics.TpsRollingAverage(300);
   private final TickStatistics.TpsRollingAverage tps15Min = new TickStatistics.TpsRollingAverage(900);
   private final TickStatistics.TpsRollingAverage[] tpsAverages;
   private boolean durationSupported;
   private final RollingAverage tickDuration10Sec;
   private final RollingAverage tickDuration1Min;
   private final RollingAverage[] tickDurationAverages;
   private long last;

   public TickStatistics() {
      this.tpsAverages = new TickStatistics.TpsRollingAverage[]{this.tps5Sec, this.tps10Sec, this.tps1Min, this.tps5Min, this.tps15Min};
      this.durationSupported = false;
      this.tickDuration10Sec = new RollingAverage(200);
      this.tickDuration1Min = new RollingAverage(1200);
      this.tickDurationAverages = new RollingAverage[]{this.tickDuration10Sec, this.tickDuration1Min};
      this.last = 0L;
   }

   public boolean isDurationSupported() {
      return this.durationSupported;
   }

   public void onTick(int currentTick) {
      if (currentTick % 20 == 0) {
         long now = System.nanoTime();
         if (this.last == 0L) {
            this.last = now;
         } else {
            long diff = now - this.last;
            BigDecimal currentTps = TPS_BASE.divide(new BigDecimal(diff), 30, RoundingMode.HALF_UP);
            BigDecimal total = currentTps.multiply(new BigDecimal(diff));

            for(TickStatistics.TpsRollingAverage rollingAverage : this.tpsAverages) {
               rollingAverage.add(currentTps, diff, total);
            }

            this.last = now;
         }
      }
   }

   public void onTick(double duration) {
      this.durationSupported = true;
      BigDecimal decimal = new BigDecimal(duration);

      for(RollingAverage rollingAverage : this.tickDurationAverages) {
         rollingAverage.add(decimal);
      }

   }

   public double tps5Sec() {
      return this.tps5Sec.getAverage();
   }

   public double tps10Sec() {
      return this.tps10Sec.getAverage();
   }

   public double tps1Min() {
      return this.tps1Min.getAverage();
   }

   public double tps5Min() {
      return this.tps5Min.getAverage();
   }

   public double tps15Min() {
      return this.tps15Min.getAverage();
   }

   public RollingAverage duration10Sec() {
      return !this.durationSupported ? null : this.tickDuration10Sec;
   }

   public RollingAverage duration1Min() {
      return !this.durationSupported ? null : this.tickDuration1Min;
   }

   public static final class TpsRollingAverage {
      private final int size;
      private long time;
      private BigDecimal total;
      private int index = 0;
      private final BigDecimal[] samples;
      private final long[] times;

      TpsRollingAverage(int size) {
         this.size = size;
         this.time = (long)size * TickStatistics.SEC_IN_NANO;
         this.total = (new BigDecimal(20)).multiply(new BigDecimal(TickStatistics.SEC_IN_NANO)).multiply(new BigDecimal(size));
         this.samples = new BigDecimal[size];
         this.times = new long[size];

         for(int i = 0; i < size; ++i) {
            this.samples[i] = new BigDecimal(20);
            this.times[i] = TickStatistics.SEC_IN_NANO;
         }

      }

      public void add(BigDecimal x, long t, BigDecimal total) {
         this.time -= this.times[this.index];
         this.total = this.total.subtract(this.samples[this.index].multiply(new BigDecimal(this.times[this.index])));
         this.samples[this.index] = x;
         this.times[this.index] = t;
         this.time += t;
         this.total = this.total.add(total);
         if (++this.index == this.size) {
            this.index = 0;
         }

      }

      public double getAverage() {
         return this.total.divide(new BigDecimal(this.time), 30, RoundingMode.HALF_UP).doubleValue();
      }
   }
}
