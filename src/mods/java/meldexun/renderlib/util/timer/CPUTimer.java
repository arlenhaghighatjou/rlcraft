package meldexun.renderlib.util.timer;

import java.util.function.IntPredicate;
import java.util.function.IntToLongFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CPUTimer extends Timer {
   private final long[] results;
   private long start;

   public CPUTimer(String name, int maxResultCount) {
      super(name, maxResultCount);
      this.results = new long[maxResultCount];
   }

   protected void updateInternal() {
      this.results[this.frame] = 0L;
   }

   protected void startInternal() {
      this.start = System.nanoTime();
   }

   protected void stopInternal() {
      this.results[this.frame] += System.nanoTime() - this.start;
   }

   public LongStream results() {
      return IntStream.range(0, this.results.length).filter((i) -> {
         return i != this.frame;
      }).mapToLong((i) -> {
         return this.results[i];
      });
   }
}
