package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.MultiMineReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.Watchdog;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiMineHandler {
   MultiMineReflect reflector;

   public MultiMineHandler() {
      try {
         this.reflector = new MultiMineReflect();
         if (ModConfig.server.multimine.stallWatchdog) {
            Watchdog.addRoutine("MultiMine Stall", new MultiMineHandler.MultiMineStallRoutine());
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup MultiMineHandler!", var2);
         ErrorUtil.logSilent("MultiMine Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   public class MultiMineStallRoutine extends Watchdog.Routine {
      private AtomicInteger semaphoreCount = new AtomicInteger(0);

      public MultiMineStallRoutine() {
      }

      public void run() throws Exception {
         if (MultiMineHandler.this.reflector.getSemaphor()) {
            int sem = this.semaphoreCount.incrementAndGet();
            if (sem > 5) {
               Watchdog.logger.warn("MultiMine may be stalling, attempting recovery");
               MultiMineHandler.this.reflector.setSemaphor(false);
               this.semaphoreCount.set(0);
            }
         } else {
            this.semaphoreCount.set(0);
         }

      }
   }
}
