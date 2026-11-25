package me.lucko.spark.common.tick;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractTickHook implements TickHook {
   private final Set<TickHook.Callback> tasks = new HashSet();
   private int tick = 0;

   public AbstractTickHook() {
   }

   protected void onTick() {
      for(TickHook.Callback r : this.tasks) {
         r.onTick(this.tick);
      }

      ++this.tick;
   }

   public int getCurrentTick() {
      return this.tick;
   }

   public void addCallback(TickHook.Callback runnable) {
      this.tasks.add(runnable);
   }

   public void removeCallback(TickHook.Callback runnable) {
      this.tasks.remove(runnable);
   }
}
