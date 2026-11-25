package com.github.benmanes.caffeine.cache;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

interface LocalLoadingCache<K, V> extends LocalManualCache<K, V>, LoadingCache<K, V> {
   Logger logger = Logger.getLogger(LocalLoadingCache.class.getName());

   CacheLoader<? super K, V> cacheLoader();

   Function<K, V> mappingFunction();

   boolean hasBulkLoader();

   default boolean hasLoadAll(CacheLoader<? super K, V> loader) {
      try {
         Method classLoadAll = loader.getClass().getMethod("loadAll", Iterable.class);
         Method defaultLoadAll = CacheLoader.class.getMethod("loadAll", Iterable.class);
         return !classLoadAll.equals(defaultLoadAll);
      } catch (SecurityException | NoSuchMethodException var4) {
         logger.log(Level.WARNING, "Cannot determine if CacheLoader can bulk load", var4);
         return false;
      }
   }

   @Nullable
   default V get(K key) {
      return (V)this.cache().computeIfAbsent(key, this.mappingFunction());
   }

   default Map<K, V> getAll(Iterable<? extends K> keys) {
      return this.hasBulkLoader() ? this.loadInBulk(keys) : this.loadSequentially(keys);
   }

   default Map<K, V> loadSequentially(Iterable<? extends K> keys) {
      Set<K> uniqueKeys = new LinkedHashSet();

      for(K key : keys) {
         uniqueKeys.add(key);
      }

      int count = 0;
      Map<K, V> result = new LinkedHashMap(uniqueKeys.size());

      try {
         for(K key : uniqueKeys) {
            ++count;
            V value = (V)this.get(key);
            if (value != null) {
               result.put(key, value);
            }
         }
      } catch (Throwable var8) {
         this.cache().statsCounter().recordMisses(uniqueKeys.size() - count);
         throw var8;
      }

      return Collections.unmodifiableMap(result);
   }

   default Map<K, V> loadInBulk(Iterable<? extends K> keys) {
      Set<K> keysToLoad = new LinkedHashSet();
      Map<K, V> found = this.cache().getAllPresent(keys);
      Map<K, V> result = new LinkedHashMap(found.size());

      for(K key : keys) {
         V value = (V)found.get(key);
         if (value == null) {
            keysToLoad.add(key);
         }

         result.put(key, value);
      }

      if (keysToLoad.isEmpty()) {
         return found;
      } else {
         this.bulkLoad(keysToLoad, result);
         return Collections.unmodifiableMap(result);
      }
   }

   default void bulkLoad(Set<K> keysToLoad, Map<K, V> result) {
      boolean success = false;
      long startTime = this.cache().statsTicker().read();
      boolean var16 = false;

      try {
         var16 = true;
         Map<K, V> e = this.cacheLoader().loadAll(keysToLoad);
         e.forEach((keyx, valuex) -> {
            this.cache().put(key, value, false);
         });

         for(K key : keysToLoad) {
            V value = (V)e.get(key);
            if (value == null) {
               result.remove(key);
            } else {
               result.put(key, value);
            }
         }

         success = !e.isEmpty();
         var16 = false;
      } catch (RuntimeException var17) {
         throw var17;
      } catch (Exception var18) {
         throw new CompletionException(var18);
      } finally {
         if (var16) {
            long loadTime = this.cache().statsTicker().read() - startTime;
            if (success) {
               this.cache().statsCounter().recordLoadSuccess(loadTime);
            } else {
               this.cache().statsCounter().recordLoadFailure(loadTime);
            }

         }
      }

      long loadTime = this.cache().statsTicker().read() - startTime;
      if (success) {
         this.cache().statsCounter().recordLoadSuccess(loadTime);
      } else {
         this.cache().statsCounter().recordLoadFailure(loadTime);
      }

   }

   default void refresh(K key) {
      Objects.requireNonNull(key);
      long[] writeTime = new long[1];
      long startTime = this.cache().statsTicker().read();
      V oldValue = (V)this.cache().getIfPresentQuietly(key, writeTime);
      CompletableFuture<V> refreshFuture = oldValue == null ? this.cacheLoader().asyncLoad(key, this.cache().executor()) : this.cacheLoader().asyncReload(key, oldValue, this.cache().executor());
      refreshFuture.whenComplete((newValue, error) -> {
         long loadTime = this.cache().statsTicker().read() - startTime;
         if (error != null) {
            logger.log(Level.WARNING, "Exception thrown during refresh", error);
            this.cache().statsCounter().recordLoadFailure(loadTime);
         } else {
            boolean[] discard = new boolean[1];
            this.cache().compute(key, (k, currentValue) -> {
               if (currentValue == null) {
                  return (void)newValue;
               } else {
                  if (currentValue == oldValue) {
                     long expectedWriteTime = writeTime[0];
                     if (this.cache().hasWriteTime()) {
                        this.cache().getIfPresentQuietly(key, writeTime);
                     }

                     if (writeTime[0] == expectedWriteTime) {
                        return (void)newValue;
                     }
                  }

                  discard[0] = true;
                  return (void)currentValue;
               }
            }, false, false, true);
            if (discard[0] && this.cache().hasRemovalListener()) {
               this.cache().notifyRemoval(key, newValue, RemovalCause.REPLACED);
            }

            if (newValue == null) {
               this.cache().statsCounter().recordLoadFailure(loadTime);
            } else {
               this.cache().statsCounter().recordLoadSuccess(loadTime);
            }

         }
      });
   }
}
