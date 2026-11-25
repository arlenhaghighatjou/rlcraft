package nonapi.io.github.classgraph.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import nonapi.io.github.classgraph.utils.LogNode;

public abstract class SingletonMap<K, V, E extends Exception> {
   private final ConcurrentMap<K, SingletonMap.SingletonHolder<V>> map = new ConcurrentHashMap();

   public SingletonMap() {
   }

   public abstract V newInstance(K var1, LogNode var2) throws E, InterruptedException;

   public V get(K key, LogNode log) throws E, InterruptedException, SingletonMap.NullSingletonException {
      SingletonMap.SingletonHolder<V> singletonHolder = (SingletonMap.SingletonHolder)this.map.get(key);
      V instance = null;
      if (singletonHolder != null) {
         instance = singletonHolder.get();
      } else {
         SingletonMap.SingletonHolder<V> newSingletonHolder = new SingletonMap.SingletonHolder<V>();
         SingletonMap.SingletonHolder<V> oldSingletonHolder = (SingletonMap.SingletonHolder)this.map.putIfAbsent(key, newSingletonHolder);
         if (oldSingletonHolder != null) {
            instance = oldSingletonHolder.get();
         } else {
            try {
               instance = (V)this.newInstance(key, log);
            } finally {
               newSingletonHolder.set(instance);
            }
         }
      }

      if (instance == null) {
         throw new SingletonMap.NullSingletonException(key);
      } else {
         return instance;
      }
   }

   public List<V> values() throws InterruptedException {
      List<V> entries = new ArrayList(this.map.size());

      for(Entry<K, SingletonMap.SingletonHolder<V>> ent : this.map.entrySet()) {
         V entryValue = (V)((SingletonMap.SingletonHolder)ent.getValue()).get();
         if (entryValue != null) {
            entries.add(entryValue);
         }
      }

      return entries;
   }

   public void clear() {
      this.map.clear();
   }

   public static class NullSingletonException extends Exception {
      static final long serialVersionUID = 1L;

      public <K> NullSingletonException(K key) {
         super("newInstance returned null for key " + key);
      }
   }

   private static class SingletonHolder<V> {
      private volatile V singleton;
      private final CountDownLatch initialized;

      private SingletonHolder() {
         this.initialized = new CountDownLatch(1);
      }

      void set(V singleton) throws IllegalArgumentException {
         if (this.initialized.getCount() < 1L) {
            throw new IllegalArgumentException("Singleton already initialized");
         } else {
            this.singleton = singleton;
            this.initialized.countDown();
            if (this.initialized.getCount() != 0L) {
               throw new IllegalArgumentException("Singleton initialized more than once");
            }
         }
      }

      V get() throws InterruptedException {
         this.initialized.await();
         return this.singleton;
      }
   }
}
