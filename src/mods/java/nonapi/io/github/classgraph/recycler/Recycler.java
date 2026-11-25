package nonapi.io.github.classgraph.recycler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Recycler<T, E extends Exception> implements AutoCloseable {
   private final Set<T> usedInstances = Collections.newSetFromMap(new ConcurrentHashMap());
   private final Queue<T> unusedInstances = new ConcurrentLinkedQueue();

   public Recycler() {
   }

   public abstract T newInstance() throws E;

   public T acquire() throws E {
      T recycledInstance = (T)this.unusedInstances.poll();
      T instance;
      if (recycledInstance == null) {
         T newInstance = (T)this.newInstance();
         if (newInstance == null) {
            throw new NullPointerException("Failed to allocate a new recyclable instance");
         }

         instance = newInstance;
      } else {
         instance = recycledInstance;
      }

      this.usedInstances.add(instance);
      return instance;
   }

   public RecycleOnClose<T, E> acquireRecycleOnClose() throws E {
      return new RecycleOnClose<T, E>(this, this.acquire());
   }

   public final void recycle(T instance) {
      if (instance != null) {
         this.usedInstances.remove(instance);
         if (instance instanceof Resettable) {
            ((Resettable)instance).reset();
         }

         this.unusedInstances.add(instance);
      }

   }

   public void close() {
      T unusedInstance;
      while((unusedInstance = (T)this.unusedInstances.poll()) != null) {
         if (unusedInstance instanceof AutoCloseable) {
            try {
               ((AutoCloseable)unusedInstance).close();
            } catch (Exception var3) {
               ;
            }
         }
      }

   }

   public void forceClose() {
      for(T usedInstance : new ArrayList(this.usedInstances)) {
         if (this.usedInstances.remove(usedInstance)) {
            this.unusedInstances.add(usedInstance);
         }
      }

      this.close();
   }
}
