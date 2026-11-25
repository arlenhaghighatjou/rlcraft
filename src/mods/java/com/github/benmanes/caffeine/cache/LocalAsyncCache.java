package com.github.benmanes.caffeine.cache;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

interface LocalAsyncCache<K, V> extends AsyncCache<K, V> {
   Logger logger = Logger.getLogger(LocalAsyncCache.class.getName());

   LocalCache<K, CompletableFuture<V>> cache();

   Policy<K, V> policy();

   @Nullable
   default CompletableFuture<V> getIfPresent(@NonNull Object key) {
      return (CompletableFuture)this.cache().getIfPresent(key, true);
   }

   default CompletableFuture<V> get(@NonNull K key, @NonNull Function<? super K, ? extends V> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      return this.get(key, (k1, executor) -> {
         return CompletableFuture.supplyAsync(() -> {
            return (CompletableFuture<V>)mappingFunction.apply(key);
         }, executor);
      });
   }

   default CompletableFuture<V> get(K key, BiFunction<? super K, Executor, CompletableFuture<V>> mappingFunction) {
      return this.get(key, mappingFunction, true);
   }

   default CompletableFuture<V> get(K key, BiFunction<? super K, Executor, CompletableFuture<V>> mappingFunction, boolean recordStats) {
      long startTime = this.cache().statsTicker().read();
      CompletableFuture<V>[] result = new CompletableFuture[1];
      CompletableFuture<V> future = (CompletableFuture)this.cache().computeIfAbsent(key, (k) -> {
         result[0] = (CompletableFuture)mappingFunction.apply(key, this.cache().executor());
         return (CompletableFuture)Objects.requireNonNull(result[0]);
      }, recordStats, false);
      if (result[0] != null) {
         this.handleCompletion(key, result[0], startTime, false);
      }

      return future;
   }

   default void put(K key, CompletableFuture<V> valueFuture) {
      if (!valueFuture.isCompletedExceptionally() && (!valueFuture.isDone() || valueFuture.join() != null)) {
         long startTime = this.cache().statsTicker().read();
         this.cache().put(key, valueFuture);
         this.handleCompletion(key, valueFuture, startTime, false);
      } else {
         this.cache().statsCounter().recordLoadFailure(0L);
         this.cache().remove(key);
      }
   }

   default void handleCompletion(K key, CompletableFuture<V> valueFuture, long startTime, boolean recordMiss) {
      AtomicBoolean completed = new AtomicBoolean();
      valueFuture.whenComplete((value, error) -> {
         if (completed.compareAndSet(false, true)) {
            long loadTime = this.cache().statsTicker().read() - startTime;
            if (value == null) {
               if (error != null) {
                  logger.log(Level.WARNING, "Exception thrown during asynchronous load", error);
               }

               this.cache().remove(key, valueFuture);
               this.cache().statsCounter().recordLoadFailure(loadTime);
               if (recordMiss) {
                  this.cache().statsCounter().recordMisses(1);
               }
            } else {
               this.cache().replace(key, valueFuture, valueFuture);
               this.cache().statsCounter().recordLoadSuccess(loadTime);
               if (recordMiss) {
                  this.cache().statsCounter().recordMisses(1);
               }
            }

         }
      });
   }

   public abstract static class AbstractCacheView<K, V> implements Cache<K, V>, Serializable {
      @Nullable
      transient LocalAsyncCache.AsMapView<K, V> asMapView;

      public AbstractCacheView() {
      }

      abstract LocalAsyncCache<K, V> asyncCache();

      @Nullable
      public V getIfPresent(Object key) {
         CompletableFuture<V> future = (CompletableFuture)this.asyncCache().cache().getIfPresent(key, true);
         return (V)Async.getIfReady(future);
      }

      public Map<K, V> getAllPresent(Iterable<?> keys) {
         Set<Object> uniqueKeys = new LinkedHashSet();

         for(Object key : keys) {
            uniqueKeys.add(key);
         }

         int misses = 0;
         Map<Object, Object> result = new LinkedHashMap();

         for(Object key : uniqueKeys) {
            CompletableFuture<V> future = (CompletableFuture)this.asyncCache().cache().get(key);
            Object value = Async.getIfReady(future);
            if (value == null) {
               ++misses;
            } else {
               result.put(key, value);
            }
         }

         this.asyncCache().cache().statsCounter().recordMisses(misses);
         this.asyncCache().cache().statsCounter().recordHits(result.size());
         return Collections.unmodifiableMap(result);
      }

      public V get(K key, Function<? super K, ? extends V> mappingFunction) {
         Objects.requireNonNull(mappingFunction);
         CompletableFuture<V> future = this.asyncCache().get(key, (k, executor) -> {
            return (V)CompletableFuture.supplyAsync(() -> {
               return (V)mappingFunction.apply(key);
            }, executor);
         });

         try {
            return (V)future.get();
         } catch (ExecutionException var5) {
            if (var5.getCause() instanceof RuntimeException) {
               throw (RuntimeException)var5.getCause();
            } else if (var5.getCause() instanceof Error) {
               throw (Error)var5.getCause();
            } else {
               throw new CompletionException(var5.getCause());
            }
         } catch (InterruptedException var6) {
            throw new CompletionException(var6);
         }
      }

      public void put(K key, V value) {
         Objects.requireNonNull(value);
         this.asyncCache().cache().put(key, CompletableFuture.completedFuture(value));
      }

      public void putAll(Map<? extends K, ? extends V> map) {
         map.forEach(this::put);
      }

      public void invalidate(Object key) {
         this.asyncCache().cache().remove(key);
      }

      public void invalidateAll(Iterable<?> keys) {
         this.asyncCache().cache().invalidateAll(keys);
      }

      public void invalidateAll() {
         this.asyncCache().cache().clear();
      }

      public long estimatedSize() {
         return (long)this.asyncCache().cache().size();
      }

      public CacheStats stats() {
         return this.asyncCache().cache().statsCounter().snapshot();
      }

      public void cleanUp() {
         this.asyncCache().cache().cleanUp();
      }

      public Policy<K, V> policy() {
         return this.asyncCache().policy();
      }

      public ConcurrentMap<K, V> asMap() {
         return this.asMapView == null ? (this.asMapView = new LocalAsyncCache.AsMapView<K, V>(this.asyncCache().cache())) : this.asMapView;
      }
   }

   public static final class AsMapView<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
      final LocalCache<K, CompletableFuture<V>> delegate;
      @Nullable
      Collection<V> values;
      @Nullable
      Set<Entry<K, V>> entries;

      AsMapView(LocalCache<K, CompletableFuture<V>> delegate) {
         this.delegate = delegate;
      }

      public boolean isEmpty() {
         return this.delegate.isEmpty();
      }

      public int size() {
         return this.delegate.size();
      }

      public void clear() {
         this.delegate.clear();
      }

      public boolean containsKey(Object key) {
         return this.delegate.containsKey(key);
      }

      public boolean containsValue(Object value) {
         Objects.requireNonNull(value);

         for(CompletableFuture<V> valueFuture : this.delegate.values()) {
            if (value.equals(Async.getIfReady(valueFuture))) {
               return true;
            }
         }

         return false;
      }

      @Nullable
      public V get(Object key) {
         return (V)Async.getIfReady((CompletableFuture)this.delegate.get(key));
      }

      @Nullable
      public V putIfAbsent(K key, V value) {
         Objects.requireNonNull(value);
         CompletableFuture<V> valueFuture = (CompletableFuture)this.delegate.putIfAbsent(key, CompletableFuture.completedFuture(value));
         return (V)Async.getWhenSuccessful(valueFuture);
      }

      @Nullable
      public V put(K key, V value) {
         Objects.requireNonNull(value);
         CompletableFuture<V> oldValueFuture = (CompletableFuture)this.delegate.put(key, CompletableFuture.completedFuture(value));
         return (V)Async.getWhenSuccessful(oldValueFuture);
      }

      @Nullable
      public V remove(Object key) {
         CompletableFuture<V> oldValueFuture = (CompletableFuture)this.delegate.remove(key);
         return (V)Async.getWhenSuccessful(oldValueFuture);
      }

      public boolean remove(Object key, Object value) {
         Objects.requireNonNull(key);
         if (value == null) {
            return false;
         } else {
            K castedKey = (K)key;
            boolean[] removed = new boolean[]{false};
            boolean[] done = new boolean[]{false};

            while(true) {
               CompletableFuture<V> future = (CompletableFuture)this.delegate.get(key);
               V oldValue = (V)Async.getWhenSuccessful(future);
               if (future != null && !value.equals(oldValue)) {
                  return false;
               }

               this.delegate.compute(castedKey, (k, oldValueFuture) -> {
                  if (future != oldValueFuture) {
                     return oldValueFuture;
                  } else {
                     done[0] = true;
                     removed[0] = value.equals(oldValue);
                     return removed[0] ? null : oldValueFuture;
                  }
               }, false, false, true);
               if (done[0]) {
                  break;
               }
            }

            return removed[0];
         }
      }

      @Nullable
      public V replace(K key, V value) {
         Objects.requireNonNull(value);
         CompletableFuture<V> oldValueFuture = (CompletableFuture)this.delegate.replace(key, CompletableFuture.completedFuture(value));
         return (V)Async.getWhenSuccessful(oldValueFuture);
      }

      public boolean replace(K key, V oldValue, V newValue) {
         Objects.requireNonNull(oldValue);
         Objects.requireNonNull(newValue);
         CompletableFuture<V> oldValueFuture = (CompletableFuture)this.delegate.get(key);
         if (oldValueFuture != null && !oldValue.equals(Async.getWhenSuccessful(oldValueFuture))) {
            return false;
         } else {
            boolean[] replaced = new boolean[]{false};
            this.delegate.compute(key, (k, value) -> {
               replaced[0] = oldValue.equals(Async.getWhenSuccessful(value));
               return (boolean)(replaced[0] ? CompletableFuture.completedFuture(newValue) : value);
            }, false, false, true);
            return replaced[0];
         }
      }

      @Nullable
      public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
         Objects.requireNonNull(mappingFunction);
         CompletableFuture<V> valueFuture = (CompletableFuture)this.delegate.computeIfAbsent(key, (k) -> {
            V newValue = (V)mappingFunction.apply(key);
            return (V)(newValue == null ? null : CompletableFuture.completedFuture(newValue));
         });
         return (V)Async.getWhenSuccessful(valueFuture);
      }

      @Nullable
      public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
         Objects.requireNonNull(remappingFunction);
         boolean[] computed = new boolean[]{false};

         CompletableFuture<V> valueFuture;
         while(true) {
            CompletableFuture<V> future = (CompletableFuture)this.delegate.get(key);
            V oldValue = (V)Async.getWhenSuccessful(future);
            if (oldValue == null) {
               return (V)null;
            }

            valueFuture = (CompletableFuture)this.delegate.computeIfPresent(key, (k, oldValueFuture) -> {
               if (future != oldValueFuture) {
                  return (V)oldValueFuture;
               } else {
                  computed[0] = true;
                  V newValue = (V)remappingFunction.apply(key, oldValue);
                  return (V)(newValue == null ? null : CompletableFuture.completedFuture(newValue));
               }
            });
            if (computed[0] || valueFuture == null) {
               break;
            }
         }

         return (V)Async.getWhenSuccessful(valueFuture);
      }

      @Nullable
      public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
         Objects.requireNonNull(remappingFunction);
         boolean[] computed = new boolean[]{false};

         CompletableFuture<V> valueFuture;
         while(true) {
            CompletableFuture<V> future = (CompletableFuture)this.delegate.get(key);
            V oldValue = (V)Async.getWhenSuccessful(future);
            valueFuture = (CompletableFuture)this.delegate.compute(key, (k, oldValueFuture) -> {
               if (future != oldValueFuture) {
                  return (V)oldValueFuture;
               } else {
                  computed[0] = true;
                  long startTime = this.delegate.statsTicker().read();
                  V newValue = (V)remappingFunction.apply(key, oldValue);
                  long loadTime = this.delegate.statsTicker().read() - startTime;
                  if (newValue == null) {
                     this.delegate.statsCounter().recordLoadFailure(loadTime);
                     return (V)null;
                  } else {
                     this.delegate.statsCounter().recordLoadSuccess(loadTime);
                     return (V)CompletableFuture.completedFuture(newValue);
                  }
               }
            }, false, false, true);
            if (computed[0]) {
               break;
            }
         }

         return (V)Async.getWhenSuccessful(valueFuture);
      }

      @Nullable
      public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         Objects.requireNonNull(value);
         Objects.requireNonNull(remappingFunction);
         CompletableFuture<V> newValueFuture = CompletableFuture.completedFuture(value);
         boolean[] merged = new boolean[]{false};

         CompletableFuture<V> mergedValueFuture;
         while(true) {
            CompletableFuture<V> future = (CompletableFuture)this.delegate.get(key);
            V oldValue = (V)Async.getWhenSuccessful(future);
            mergedValueFuture = (CompletableFuture)this.delegate.merge(key, newValueFuture, (oldValueFuture, valueFuture) -> {
               if (future != oldValueFuture) {
                  return (V)oldValueFuture;
               } else {
                  merged[0] = true;
                  if (oldValue == null) {
                     return (V)valueFuture;
                  } else {
                     V mergedValue = (V)remappingFunction.apply(oldValue, value);
                     if (mergedValue == null) {
                        return (V)null;
                     } else if (mergedValue == oldValue) {
                        return (V)oldValueFuture;
                     } else {
                        return (V)(mergedValue == value ? valueFuture : CompletableFuture.completedFuture(mergedValue));
                     }
                  }
               }
            });
            if (merged[0] || mergedValueFuture == newValueFuture) {
               break;
            }
         }

         return (V)Async.getWhenSuccessful(mergedValueFuture);
      }

      public Set<K> keySet() {
         return this.delegate.keySet();
      }

      public Collection<V> values() {
         return this.values == null ? (this.values = new LocalAsyncCache.AsMapView.Values()) : this.values;
      }

      public Set<Entry<K, V>> entrySet() {
         return this.entries == null ? (this.entries = new LocalAsyncCache.AsMapView.EntrySet()) : this.entries;
      }

      private final class EntrySet extends AbstractSet<Entry<K, V>> {
         private EntrySet() {
         }

         public boolean isEmpty() {
            return AsMapView.this.isEmpty();
         }

         public int size() {
            return AsMapView.this.size();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
               return false;
            } else {
               Entry<?, ?> entry = (Entry)o;
               V value = (V)AsMapView.this.get(entry.getKey());
               return value != null && value.equals(entry.getValue());
            }
         }

         public boolean remove(Object obj) {
            if (!(obj instanceof Entry)) {
               return false;
            } else {
               Entry<?, ?> entry = (Entry)obj;
               return AsMapView.this.remove(entry.getKey(), entry.getValue());
            }
         }

         public void clear() {
            AsMapView.this.clear();
         }

         public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
               Iterator<Entry<K, CompletableFuture<V>>> iterator;
               @Nullable
               Entry<K, V> cursor;
               @Nullable
               K removalKey;

               {
                  this.iterator = AsMapView.this.delegate.entrySet().iterator();
               }

               public boolean hasNext() {
                  while(this.cursor == null && this.iterator.hasNext()) {
                     Entry<K, CompletableFuture<V>> entry = (Entry)this.iterator.next();
                     V value = (V)Async.getIfReady((CompletableFuture)entry.getValue());
                     if (value != null) {
                        this.cursor = new WriteThroughEntry<K, V>(AsMapView.this, entry.getKey(), value);
                     }
                  }

                  return this.cursor != null;
               }

               public Entry<K, V> next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     K key = (K)this.cursor.getKey();
                     Entry<K, V> entry = this.cursor;
                     this.removalKey = key;
                     this.cursor = null;
                     return entry;
                  }
               }

               public void remove() {
                  Caffeine.requireState(this.removalKey != null);
                  AsMapView.this.delegate.remove(this.removalKey);
                  this.removalKey = (K)null;
               }
            };
         }
      }

      private final class Values extends AbstractCollection<V> {
         private Values() {
         }

         public boolean isEmpty() {
            return AsMapView.this.isEmpty();
         }

         public int size() {
            return AsMapView.this.size();
         }

         public boolean contains(Object o) {
            return AsMapView.this.containsValue(o);
         }

         public void clear() {
            AsMapView.this.clear();
         }

         public Iterator<V> iterator() {
            return new Iterator<V>() {
               Iterator<Entry<K, V>> iterator = AsMapView.this.entrySet().iterator();

               public boolean hasNext() {
                  return this.iterator.hasNext();
               }

               public V next() {
                  return (V)((Entry)this.iterator.next()).getValue();
               }

               public void remove() {
                  this.iterator.remove();
               }
            };
         }
      }
   }

   public static final class AsyncAsMapView<K, V> implements ConcurrentMap<K, CompletableFuture<V>> {
      final LocalAsyncCache<K, V> asyncCache;

      AsyncAsMapView(LocalAsyncCache<K, V> asyncCache) {
         this.asyncCache = (LocalAsyncCache)Objects.requireNonNull(asyncCache);
      }

      public boolean isEmpty() {
         return this.asyncCache.cache().isEmpty();
      }

      public int size() {
         return this.asyncCache.cache().size();
      }

      public void clear() {
         this.asyncCache.cache().clear();
      }

      public boolean containsKey(Object key) {
         return this.asyncCache.cache().containsKey(key);
      }

      public boolean containsValue(Object value) {
         return this.asyncCache.cache().containsValue(value);
      }

      @Nullable
      public CompletableFuture<V> get(Object key) {
         return (CompletableFuture)this.asyncCache.cache().get(key);
      }

      public CompletableFuture<V> putIfAbsent(K key, CompletableFuture<V> value) {
         CompletableFuture<V> prior = (CompletableFuture)this.asyncCache.cache().putIfAbsent(key, value);
         long startTime = this.asyncCache.cache().statsTicker().read();
         if (prior == null) {
            this.asyncCache.handleCompletion(key, value, startTime, false);
         }

         return prior;
      }

      public CompletableFuture<V> put(K key, CompletableFuture<V> value) {
         CompletableFuture<V> prior = (CompletableFuture)this.asyncCache.cache().put(key, value);
         long startTime = this.asyncCache.cache().statsTicker().read();
         this.asyncCache.handleCompletion(key, value, startTime, false);
         return prior;
      }

      public void putAll(Map<? extends K, ? extends CompletableFuture<V>> map) {
         map.forEach(this::put);
      }

      public CompletableFuture<V> replace(K key, CompletableFuture<V> value) {
         CompletableFuture<V> prior = (CompletableFuture)this.asyncCache.cache().replace(key, value);
         long startTime = this.asyncCache.cache().statsTicker().read();
         if (prior != null) {
            this.asyncCache.handleCompletion(key, value, startTime, false);
         }

         return prior;
      }

      public boolean replace(K key, CompletableFuture<V> oldValue, CompletableFuture<V> newValue) {
         boolean replaced = this.asyncCache.cache().replace(key, oldValue, newValue);
         long startTime = this.asyncCache.cache().statsTicker().read();
         if (replaced) {
            this.asyncCache.handleCompletion(key, newValue, startTime, false);
         }

         return replaced;
      }

      public CompletableFuture<V> remove(Object key) {
         return (CompletableFuture)this.asyncCache.cache().remove(key);
      }

      public boolean remove(Object key, Object value) {
         return this.asyncCache.cache().remove(key, value);
      }

      @Nullable
      public CompletableFuture<V> computeIfAbsent(K key, Function<? super K, ? extends CompletableFuture<V>> mappingFunction) {
         Objects.requireNonNull(mappingFunction);
         CompletableFuture<V>[] result = new CompletableFuture[1];
         long startTime = this.asyncCache.cache().statsTicker().read();
         CompletableFuture<V> future = (CompletableFuture)this.asyncCache.cache().computeIfAbsent(key, (k) -> {
            result[0] = (CompletableFuture)mappingFunction.apply(k);
            return result[0];
         }, false, false);
         if (result[0] == null) {
            if (future != null && this.asyncCache.cache().isRecordingStats()) {
               future.whenComplete((r, e) -> {
                  if (r != null || e == null) {
                     this.asyncCache.cache().statsCounter().recordHits(1);
                  }

               });
            }
         } else {
            this.asyncCache.handleCompletion(key, result[0], startTime, true);
         }

         return future;
      }

      public CompletableFuture<V> computeIfPresent(K key, BiFunction<? super K, ? super CompletableFuture<V>, ? extends CompletableFuture<V>> remappingFunction) {
         Objects.requireNonNull(remappingFunction);
         CompletableFuture<V>[] result = new CompletableFuture[1];
         long startTime = this.asyncCache.cache().statsTicker().read();
         this.asyncCache.cache().compute(key, (k, oldValue) -> {
            result[0] = oldValue == null ? null : (CompletableFuture)remappingFunction.apply(k, oldValue);
            return result[0];
         }, false, false, false);
         if (result[0] != null) {
            this.asyncCache.handleCompletion(key, result[0], startTime, false);
         }

         return result[0];
      }

      public CompletableFuture<V> compute(K key, BiFunction<? super K, ? super CompletableFuture<V>, ? extends CompletableFuture<V>> remappingFunction) {
         Objects.requireNonNull(remappingFunction);
         CompletableFuture<V>[] result = new CompletableFuture[1];
         long startTime = this.asyncCache.cache().statsTicker().read();
         this.asyncCache.cache().compute(key, (k, oldValue) -> {
            result[0] = (CompletableFuture)remappingFunction.apply(k, oldValue);
            return result[0];
         }, false, false, false);
         if (result[0] != null) {
            this.asyncCache.handleCompletion(key, result[0], startTime, false);
         }

         return result[0];
      }

      public CompletableFuture<V> merge(K key, CompletableFuture<V> value, BiFunction<? super CompletableFuture<V>, ? super CompletableFuture<V>, ? extends CompletableFuture<V>> remappingFunction) {
         Objects.requireNonNull(value);
         Objects.requireNonNull(remappingFunction);
         CompletableFuture<V>[] result = new CompletableFuture[1];
         long startTime = this.asyncCache.cache().statsTicker().read();
         this.asyncCache.cache().compute(key, (k, oldValue) -> {
            result[0] = oldValue == null ? value : (CompletableFuture)remappingFunction.apply(oldValue, value);
            return result[0];
         }, false, false, false);
         if (result[0] != null) {
            this.asyncCache.handleCompletion(key, result[0], startTime, false);
         }

         return result[0];
      }

      public Set<K> keySet() {
         return this.asyncCache.cache().keySet();
      }

      public Collection<CompletableFuture<V>> values() {
         return this.asyncCache.cache().values();
      }

      public Set<Entry<K, CompletableFuture<V>>> entrySet() {
         return this.asyncCache.cache().entrySet();
      }

      public boolean equals(Object o) {
         return this.asyncCache.cache().equals(o);
      }

      public int hashCode() {
         return this.asyncCache.cache().hashCode();
      }

      public String toString() {
         return this.asyncCache.cache().toString();
      }
   }

   public static final class CacheView<K, V> extends LocalAsyncCache.AbstractCacheView<K, V> {
      private static final long serialVersionUID = 1L;
      final LocalAsyncCache<K, V> asyncCache;

      CacheView(LocalAsyncCache<K, V> asyncCache) {
         this.asyncCache = (LocalAsyncCache)Objects.requireNonNull(asyncCache);
      }

      LocalAsyncCache<K, V> asyncCache() {
         return this.asyncCache;
      }
   }
}
