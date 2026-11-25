package betterquesting.api2.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BQThreadedIO {
   public static final BQThreadedIO INSTANCE = new BQThreadedIO();
   private ExecutorService exService;

   public BQThreadedIO() {
      this.init();
   }

   public void init() {
      if (this.exService == null || this.exService.isShutdown()) {
         this.exService = Executors.newSingleThreadExecutor();
      }

   }

   public void shutdown() {
      this.exService.shutdownNow();
   }

   public void enqueue(Runnable job) {
      if (this.exService != null && !this.exService.isShutdown()) {
         if (job == null) {
            throw new NullPointerException("Attempted to schedule null job!");
         } else {
            this.exService.submit(job);
         }
      } else {
         throw new RuntimeException("Attempted to schedule task before service was initialised!");
      }
   }

   public <T> Future<T> enqueue(Callable<T> job) {
      if (this.exService != null && !this.exService.isShutdown()) {
         if (job == null) {
            throw new NullPointerException("Attempted to schedule null job!");
         } else {
            return this.exService.submit(job);
         }
      } else {
         throw new RuntimeException("Attempted to schedule task before service was initialised!");
      }
   }
}
