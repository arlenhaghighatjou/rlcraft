package com.github.benmanes.caffeine.cache;

class SIMWW<K, V> extends SIMW<K, V> {
   final Ticker ticker;
   final WriteOrderDeque<Node<K, V>> writeOrderDeque;
   volatile long expiresAfterWriteNanos;

   SIMWW(Caffeine<K, V> builder, CacheLoader<? super K, V> cacheLoader, boolean async) {
      super(builder, cacheLoader, async);
      this.ticker = builder.getTicker();
      this.writeOrderDeque = new WriteOrderDeque<Node<K, V>>();
      this.expiresAfterWriteNanos = builder.getExpiresAfterWriteNanos();
   }

   public final Ticker expirationTicker() {
      return this.ticker;
   }

   protected final WriteOrderDeque<Node<K, V>> writeOrderDeque() {
      return this.writeOrderDeque;
   }

   protected final boolean expiresAfterWrite() {
      return true;
   }

   protected final long expiresAfterWriteNanos() {
      return this.expiresAfterWriteNanos;
   }

   protected final void setExpiresAfterWriteNanos(long expiresAfterWriteNanos) {
      this.expiresAfterWriteNanos = expiresAfterWriteNanos;
   }
}
