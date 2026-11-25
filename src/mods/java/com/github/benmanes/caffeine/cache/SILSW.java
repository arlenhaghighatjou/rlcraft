package com.github.benmanes.caffeine.cache;

class SILSW<K, V> extends SILS<K, V> {
   final Ticker ticker;
   final WriteOrderDeque<Node<K, V>> writeOrderDeque;
   volatile long expiresAfterWriteNanos;
   final MpscGrowableArrayQueue<Runnable> writeBuffer;

   SILSW(Caffeine<K, V> builder, CacheLoader<? super K, V> cacheLoader, boolean async) {
      super(builder, cacheLoader, async);
      this.ticker = builder.getTicker();
      this.writeOrderDeque = new WriteOrderDeque<Node<K, V>>();
      this.expiresAfterWriteNanos = builder.getExpiresAfterWriteNanos();
      this.writeBuffer = new MpscGrowableArrayQueue<Runnable>(4, WRITE_BUFFER_MAX);
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

   protected final MpscGrowableArrayQueue<Runnable> writeBuffer() {
      return this.writeBuffer;
   }

   protected final boolean buffersWrites() {
      return true;
   }
}
