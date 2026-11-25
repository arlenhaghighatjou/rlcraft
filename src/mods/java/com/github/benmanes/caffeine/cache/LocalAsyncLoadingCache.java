package com.github.benmanes.caffeine.cache;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class LocalAsyncLoadingCache<K, V> implements LocalAsyncCache<K, V>, AsyncLoadingCache<K, V> {
   static final Logger logger = Logger.getLogger(LocalAsyncLoadingCache.class.getName());
   final boolean canBulkLoad;
   final AsyncCacheLoader<K, V> loader;
   @Nullable
   LocalAsyncLoadingCache.LoadingCacheView<K, V> cacheView;

   LocalAsyncLoadingCache(AsyncCacheLoader<? super K, V> loader) {
      this.loader = loader;
      this.canBulkLoad = canBulkLoad(loader);
   }

   private static boolean canBulkLoad(AsyncCacheLoader<?, ?> loader) {
      try {
         Class<?> defaultLoaderClass = AsyncCacheLoader.class;
         if (loader instanceof CacheLoader) {
            defaultLoaderClass = CacheLoader.class;
            Method classLoadAll = loader.getClass().getMethod("loadAll", Iterable.class);
            Method defaultLoadAll = CacheLoader.class.getMethod("loadAll", Iterable.class);
            if (!classLoadAll.equals(defaultLoadAll)) {
               return true;
            }
         }

         Method classAsyncLoadAll = loader.getClass().getMethod("asyncLoadAll", Iterable.class, Executor.class);
         Method defaultAsyncLoadAll = defaultLoaderClass.getMethod("asyncLoadAll", Iterable.class, Executor.class);
         return !classAsyncLoadAll.equals(defaultAsyncLoadAll);
      } catch (SecurityException | NoSuchMethodException var4) {
         logger.log(Level.WARNING, "Cannot determine if CacheLoader can bulk load", var4);
         return false;
      }
   }

   public CompletableFuture<V> get(K key) {
      AsyncCacheLoader var10002 = this.loader;
      Objects.requireNonNull(this.loader);
      return this.get(key, var10002::asyncLoad);
   }

   public CompletableFuture<Map<K, V>> getAll(Iterable<? extends K> keys) {
      if (this.canBulkLoad) {
         return this.getAllBulk(keys);
      } else {
         Map<K, CompletableFuture<V>> result = new LinkedHashMap();
         Function<K, CompletableFuture<V>> mappingFunction = this::get;

         for(K key : keys) {
            CompletableFuture<V> future = (CompletableFuture)result.computeIfAbsent(key, mappingFunction);
            Objects.requireNonNull(future);
         }

         return this.composeResult(result);
      }
   }

   private CompletableFuture<Map<K, V>> getAllBulk(Iterable<? extends K> keys) {
      Map<K, CompletableFuture<V>> futures = new LinkedHashMap();
      Map<K, CompletableFuture<V>> proxies = new HashMap();

      for(K key : keys) {
         if (!futures.containsKey(key)) {
            CompletableFuture<V> future = (CompletableFuture)this.cache().getIfPresent(key, false);
            if (future == null) {
               CompletableFuture<V> proxy = new CompletableFuture();
               future = (CompletableFuture)this.cache().putIfAbsent(key, proxy);
               if (future == null) {
                  future = proxy;
                  proxies.put(key, proxy);
               }
            }

            futures.put(key, future);
         }
      }

      this.cache().statsCounter().recordMisses(proxies.size());
      this.cache().statsCounter().recordHits(futures.size() - proxies.size());
      if (proxies.isEmpty()) {
         return this.composeResult(futures);
      } else {
         LocalAsyncLoadingCache<K, V>.AsyncBulkCompleter completer = new LocalAsyncLoadingCache.AsyncBulkCompleter(proxies);

         try {
            this.loader.asyncLoadAll(proxies.keySet(), this.cache().executor()).whenComplete(completer);
            return this.composeResult(futures);
         } catch (Throwable var8) {
            completer.accept((Map)null, var8);
            throw var8;
         }
      }
   }

   private CompletableFuture<Map<K, V>> composeResult(Map<K, CompletableFuture<V>> futures) {
      if (futures.isEmpty()) {
         return CompletableFuture.completedFuture(Collections.emptyMap());
      } else {
         CompletableFuture<?>[] array = (CompletableFuture[])futures.values().toArray(new CompletableFuture[0]);
         return CompletableFuture.allOf(array).thenApply((ignored) -> {
            Map<K, V> result = new LinkedHashMap(futures.size());
            futures.forEach((key, future) -> {
               V value = (V)future.getNow((Object)null);
               if (value != null) {
                  result.put(key, value);
               }

            });
            return Collections.unmodifiableMap(result);
         });
      }
   }

   public LoadingCache<K, V> synchronous() {
      return this.cacheView == null ? (this.cacheView = new LocalAsyncLoadingCache.LoadingCacheView<K, V>(this)) : this.cacheView;
   }

   private final class AsyncBulkCompleter implements BiConsumer<Map<K, V>, Throwable> {
      private final Map<K, CompletableFuture<V>> proxies;
      private final long startTime = LocalAsyncLoadingCache.this.cache().statsTicker().read();

      AsyncBulkCompleter(Map<K, CompletableFuture<V>> proxies) {
         this.proxies = proxies;
      }

      public void accept(@Nullable Map<K, V> result, @Nullable Throwable error) {
         long loadTime = LocalAsyncLoadingCache.this.cache().statsTicker().read() - this.startTime;
         if (result == null) {
            if (error == null) {
               error = new CompletionException("null map", (Throwable)null);
            }

            for(Entry<K, CompletableFuture<V>> entry : this.proxies.entrySet()) {
               LocalAsyncLoadingCache.this.cache().remove(entry.getKey(), entry.getValue());
               ((CompletableFuture)entry.getValue()).obtrudeException(error);
            }

            LocalAsyncLoadingCache.this.cache().statsCounter().recordLoadFailure(loadTime);
            LocalAsyncLoadingCache.logger.log(Level.WARNING, "Exception thrown during asynchronous load", error);
         } else {
            this.fillProxies(result);
            this.addNewEntries(result);
            LocalAsyncLoadingCache.this.cache().statsCounter().recordLoadSuccess(loadTime);
         }

      }

      private void fillProxies(Map<K, V> result) {
         this.proxies.forEach((key, future) -> {
            V value = (V)result.get(key);
            future.obtrudeValue(value);
            if (value == null) {
               LocalAsyncLoadingCache.this.cache().remove(key, future);
            } else {
               LocalAsyncLoadingCache.this.cache().replace(key, future, future);
            }

         });
      }

      private void addNewEntries(Map<K, V> result) {
         if (this.proxies.size() != result.size()) {
            result.forEach((key, value) -> {
               if (!this.proxies.containsKey(key)) {
                  LocalAsyncLoadingCache.this.cache().put(key, CompletableFuture.completedFuture(value));
               }

            });
         }
      }
   }

   static final class LoadingCacheView<K, V> extends LocalAsyncCache.AbstractCacheView<K, V> implements LoadingCache<K, V> {
      private static final long serialVersionUID = 1L;
      final LocalAsyncLoadingCache<K, V> asyncCache;

      LoadingCacheView(LocalAsyncLoadingCache<K, V> asyncCache) {
         this.asyncCache = (LocalAsyncLoadingCache)Objects.requireNonNull(asyncCache);
      }

      LocalAsyncLoadingCache<K, V> asyncCache() {
         return this.asyncCache;
      }

      public V get(K key) {
         try {
            return (V)this.asyncCache.get(key).get();
         } catch (ExecutionException var3) {
            if (var3.getCause() instanceof RuntimeException) {
               throw (RuntimeException)var3.getCause();
            } else if (var3.getCause() instanceof Error) {
               throw (Error)var3.getCause();
            } else {
               throw new CompletionException(var3.getCause());
            }
         } catch (InterruptedException var4) {
            throw new CompletionException(var4);
         }
      }

      public Map<K, V> getAll(Iterable<? extends K> keys) {
         try {
            return (Map)this.asyncCache.getAll(keys).get();
         } catch (ExecutionException var3) {
            if (var3.getCause() instanceof RuntimeException) {
               throw (RuntimeException)var3.getCause();
            } else if (var3.getCause() instanceof Error) {
               throw (Error)var3.getCause();
            } else {
               throw new CompletionException(var3.getCause());
            }
         } catch (InterruptedException var4) {
            throw new CompletionException(var4);
         }
      }

      public void refresh(K key) {
         Objects.requireNonNull(key);
         long[] writeTime = new long[1];
         CompletableFuture<V> oldValueFuture = (CompletableFuture)this.asyncCache.cache().getIfPresentQuietly(key, writeTime);
         if (oldValueFuture != null && (!oldValueFuture.isDone() || !oldValueFuture.isCompletedExceptionally())) {
            if (oldValueFuture.isDone()) {
               oldValueFuture.thenAccept((oldValue) -> {
                  long now = this.asyncCache.cache().statsTicker().read();
                  CompletableFuture<V> refreshFuture = oldValue == null ? this.asyncCache.loader.asyncLoad(key, this.asyncCache.cache().executor()) : this.asyncCache.loader.asyncReload(key, oldValue, this.asyncCache.cache().executor());
                  refreshFuture.whenComplete((newValue, error) -> {
                     long loadTime = this.asyncCache.cache().statsTicker().read() - now;
                     if (error != null) {
                        this.asyncCache.cache().statsCounter().recordLoadFailure(loadTime);
                        LocalAsyncLoadingCache.logger.log(Level.WARNING, "Exception thrown during refresh", error);
                     } else {
                        boolean[] discard = new boolean[1];
                        this.asyncCache.cache().compute(key, (k, currentValue) -> {
                           if (currentValue == null) {
                              return (void)(newValue == null ? null : refreshFuture);
                           } else {
                              if (currentValue == oldValueFuture) {
                                 long expectedWriteTime = writeTime[0];
                                 if (this.asyncCache.cache().hasWriteTime()) {
                                    this.asyncCache.cache().getIfPresentQuietly(key, writeTime);
                                 }

                                 if (writeTime[0] == expectedWriteTime) {
                                    return (void)(newValue == null ? null : refreshFuture);
                                 }
                              }

                              discard[0] = true;
                              return (void)currentValue;
                           }
                        }, false, false, true);
                        if (discard[0] && this.asyncCache.cache().hasRemovalListener()) {
                           this.asyncCache.cache().notifyRemoval(key, refreshFuture, RemovalCause.REPLACED);
                        }

                        if (newValue == null) {
                           this.asyncCache.cache().statsCounter().recordLoadFailure(loadTime);
                        } else {
                           this.asyncCache.cache().statsCounter().recordLoadSuccess(loadTime);
                        }

                     }
                  });
               });
            }
         } else {
            LocalAsyncLoadingCache var10000 = this.asyncCache;
            AsyncCacheLoader var10002 = this.asyncCache.loader;
            Objects.requireNonNull(this.asyncCache.loader);
            var10000.get(key, var10002::asyncLoad, false);
         }
      }
   }
}
