package nonapi.io.github.classgraph.concurrency;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleThreadFactory implements ThreadFactory {
   private final String threadNamePrefix;
   private static final AtomicInteger threadIdx = new AtomicInteger();
   private final boolean daemon;

   SimpleThreadFactory(String threadNamePrefix, boolean daemon) {
      this.threadNamePrefix = threadNamePrefix;
      this.daemon = daemon;
   }

   public Thread newThread(Runnable runnable) {
      SecurityManager s = System.getSecurityManager();
      Thread thread = new Thread(s != null ? s.getThreadGroup() : new ThreadGroup("ClassGraph-thread-group"), runnable, this.threadNamePrefix + threadIdx.getAndIncrement());
      thread.setDaemon(this.daemon);
      return thread;
   }
}
