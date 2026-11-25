package me.lucko.spark.common.tick;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractTickReporter implements TickReporter {
   private final Set<TickReporter.Callback> tasks = new HashSet();

   public AbstractTickReporter() {
   }

   protected void onTick(double duration) {
      for(TickReporter.Callback r : this.tasks) {
         r.onTick(duration);
      }

   }

   public void addCallback(TickReporter.Callback runnable) {
      this.tasks.add(runnable);
   }

   public void removeCallback(TickReporter.Callback runnable) {
      this.tasks.remove(runnable);
   }
}
