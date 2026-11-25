package com.github.benmanes.caffeine.cache;

class SSL<K, V> extends SS<K, V> {
   final RemovalListener<K, V> removalListener;

   SSL(Caffeine<K, V> builder, CacheLoader<? super K, V> cacheLoader, boolean async) {
      super(builder, cacheLoader, async);
      this.removalListener = builder.<K, V>getRemovalListener(async);
   }

   public final RemovalListener<K, V> removalListener() {
      return this.removalListener;
   }

   public final boolean hasRemovalListener() {
      return true;
   }
}
