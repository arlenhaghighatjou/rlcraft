package nonapi.io.github.classgraph.concurrency;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import nonapi.io.github.classgraph.utils.LogNode;

public class WorkQueue<T> implements AutoCloseable {
   private final WorkQueue.WorkUnitProcessor<T> workUnitProcessor;
   private final BlockingQueue<WorkQueue.WorkUnitWrapper<T>> workUnits = new LinkedBlockingQueue();
   private final int numWorkers;
   private final AtomicInteger numIncompleteWorkUnits = new AtomicInteger();
   private final ConcurrentLinkedQueue<Future<?>> workerFutures = new ConcurrentLinkedQueue();
   private final InterruptionChecker interruptionChecker;
   private final LogNode log;

   public static <U> void runWorkQueue(Collection<U> elements, ExecutorService executorService, InterruptionChecker interruptionChecker, int numParallelTasks, LogNode log, WorkQueue.WorkUnitProcessor<U> workUnitProcessor) throws InterruptedException, ExecutionException {
      if (!elements.isEmpty()) {
         WorkQueue<U> workQueue = new WorkQueue<U>(elements, workUnitProcessor, numParallelTasks, interruptionChecker, log);

         try {
            workQueue.startWorkers(executorService, numParallelTasks - 1);
            workQueue.runWorkLoop();
         } catch (Throwable var10) {
            try {
               workQueue.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }

            throw var10;
         }

         workQueue.close();
      }
   }

   private WorkQueue(Collection<T> initialWorkUnits, WorkQueue.WorkUnitProcessor<T> workUnitProcessor, int numWorkers, InterruptionChecker interruptionChecker, LogNode log) {
      this.workUnitProcessor = workUnitProcessor;
      this.numWorkers = numWorkers;
      this.interruptionChecker = interruptionChecker;
      this.log = log;
      this.addWorkUnits(initialWorkUnits);
   }

   private void startWorkers(ExecutorService executorService, int numTasks) {
      for(int i = 0; i < numTasks; ++i) {
         this.workerFutures.add(executorService.submit(new Callable<Void>() {
            public Void call() throws Exception {
               WorkQueue.this.runWorkLoop();
               return null;
            }
         }));
      }

   }

   private void sendPoisonPills() {
      for(int i = 0; i < this.numWorkers; ++i) {
         this.workUnits.add(new WorkQueue.WorkUnitWrapper((Object)null));
      }

   }

   private void runWorkLoop() throws InterruptedException, ExecutionException {
      int workUnitIdx = 0;

      while(true) {
         this.interruptionChecker.check();
         WorkQueue.WorkUnitWrapper<T> workUnitWrapper = (WorkQueue.WorkUnitWrapper)this.workUnits.take();
         if (workUnitWrapper.workUnit == null) {
            return;
         }

         try {
            this.workUnitProcessor.processWorkUnit(workUnitWrapper.workUnit, workUnitIdx, this, this.log);
         } catch (OutOfMemoryError | InterruptedException var8) {
            this.workUnits.clear();
            this.sendPoisonPills();
            throw var8;
         } catch (RuntimeException var9) {
            this.workUnits.clear();
            this.sendPoisonPills();
            throw new ExecutionException("Worker thread threw unchecked exception", var9);
         } finally {
            if (this.numIncompleteWorkUnits.decrementAndGet() == 0) {
               this.sendPoisonPills();
            }

         }

         ++workUnitIdx;
      }
   }

   public void addWorkUnit(T workUnit) {
      if (workUnit == null) {
         throw new NullPointerException("workUnit cannot be null");
      } else {
         this.numIncompleteWorkUnits.incrementAndGet();
         this.workUnits.add(new WorkQueue.WorkUnitWrapper(workUnit));
      }
   }

   public void addWorkUnits(Collection<T> workUnits) {
      for(T workUnit : workUnits) {
         this.addWorkUnit(workUnit);
      }

   }

   public void close() throws ExecutionException {
      Future<?> future;
      while((future = (Future)this.workerFutures.poll()) != null) {
         try {
            future.get();
         } catch (CancellationException var3) {
            if (this.log != null) {
               this.log.log("~", "Worker thread was cancelled");
            }
         } catch (InterruptedException var4) {
            if (this.log != null) {
               this.log.log("~", "Worker thread was interrupted");
            }

            this.interruptionChecker.interrupt();
         } catch (ExecutionException var5) {
            this.interruptionChecker.setExecutionException(var5);
            this.interruptionChecker.interrupt();
         }
      }

   }

   public interface WorkUnitProcessor<T> {
      void processWorkUnit(T var1, int var2, WorkQueue<T> var3, LogNode var4) throws InterruptedException;
   }

   private static class WorkUnitWrapper<T> {
      final T workUnit;

      public WorkUnitWrapper(T workUnit) {
         this.workUnit = workUnit;
      }
   }
}
