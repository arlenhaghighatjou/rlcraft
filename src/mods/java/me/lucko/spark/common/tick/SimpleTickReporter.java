package me.lucko.spark.common.tick;

public abstract class SimpleTickReporter extends AbstractTickReporter {
   private boolean closed = false;
   private long start = 0L;

   public SimpleTickReporter() {
   }

   protected void onStart() {
      if (!this.closed) {
         this.start = System.nanoTime();
      }
   }

   protected void onEnd() {
      if (!this.closed && this.start != 0L) {
         double duration = (double)(System.nanoTime() - this.start) / 1000000.0D;
         this.onTick(duration);
      }
   }

   public void close() {
      this.closed = true;
   }
}
