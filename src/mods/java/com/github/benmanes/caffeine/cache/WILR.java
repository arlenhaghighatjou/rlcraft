package com.github.benmanes.caffeine.cache;

final class WILR<K, V> extends WIL<K, V> {
   final Ticker ticker;
   volatile long refreshAfterWriteNanos;
   final MpscGrowableArrayQueue<Runnable> writeBuffer;

   WILR(Caffeine<K, V> builder, CacheLoader<? super K, V> cacheLoader, boolean async) {
      super(builder, cacheLoader, async);
      this.ticker = builder.getTicker();
      this.refreshAfterWriteNanos = builder.getRefreshAfterWriteNanos();
      this.writeBuffer = new MpscGrowableArrayQueue<Runnable>(4, WRITE_BUFFER_MAX);
   }

   public Ticker expirationTicker() {
      return this.ticker;
   }

   protected boolean refreshAfterWrite() {
      return true;
   }

   protected long refreshAfterWriteNanos() {
      return this.refreshAfterWriteNanos;
   }

   protected void setRefreshAfterWriteNanos(long refreshAfterWriteNanos) {
      this.refreshAfterWriteNanos = refreshAfterWriteNanos;
   }

   protected MpscGrowableArrayQueue<Runnable> writeBuffer() {
      return this.writeBuffer;
   }

   protected boolean buffersWrites() {
      return true;
   }
}
