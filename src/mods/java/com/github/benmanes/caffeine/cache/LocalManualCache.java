package com.github.benmanes.caffeine.cache;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

interface LocalManualCache<K, V> extends Cache<K, V> {
   LocalCache<K, V> cache();

   default long estimatedSize() {
      return this.cache().estimatedSize();
   }

   default void cleanUp() {
      this.cache().cleanUp();
   }

   @Nullable
   default V getIfPresent(Object key) {
      return (V)this.cache().getIfPresent(key, true);
   }

   @Nullable
   default V get(K key, Function<? super K, ? extends V> mappingFunction) {
      return (V)this.cache().computeIfAbsent(key, mappingFunction);
   }

   default Map<K, V> getAllPresent(Iterable<?> keys) {
      return this.cache().getAllPresent(keys);
   }

   default void put(K key, V value) {
      this.cache().put(key, value);
   }

   default void putAll(Map<? extends K, ? extends V> map) {
      this.cache().putAll(map);
   }

   default void invalidate(Object key) {
      this.cache().remove(key);
   }

   default void invalidateAll(Iterable<?> keys) {
      this.cache().invalidateAll(keys);
   }

   default void invalidateAll() {
      this.cache().clear();
   }

   default CacheStats stats() {
      return this.cache().statsCounter().snapshot();
   }

   default ConcurrentMap<K, V> asMap() {
      return this.cache();
   }
}
