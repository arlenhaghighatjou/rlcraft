package com.github.benmanes.caffeine.cache;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Cache<K, V> {
   @Nullable
   V getIfPresent(@NonNull Object var1);

   @Nullable
   V get(@NonNull K var1, @NonNull Function<? super K, ? extends V> var2);

   @NonNull
   Map<K, V> getAllPresent(@NonNull Iterable<?> var1);

   void put(@NonNull K var1, @NonNull V var2);

   void putAll(@NonNull Map<? extends K, ? extends V> var1);

   void invalidate(@NonNull Object var1);

   void invalidateAll(@NonNull Iterable<?> var1);

   void invalidateAll();

   @NonNegative
   long estimatedSize();

   @NonNull
   CacheStats stats();

   @NonNull
   ConcurrentMap<K, V> asMap();

   void cleanUp();

   @NonNull
   Policy<K, V> policy();
}
