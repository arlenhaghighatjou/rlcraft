package com.github.benmanes.caffeine.cache;

class WIA<K, V> extends WI<K, V> {
   final Ticker ticker;
   final AccessOrderDeque<Node<K, V>> accessOrderWindowDeque;
   final Expiry<K, V> expiry;
   final TimerWheel<K, V> timerWheel;
   volatile long expiresAfterAccessNanos;
   final MpscGrowableArrayQueue<Runnable> writeBuffer;

   WIA(Caffeine<K, V> builder, CacheLoader<? super K, V> cacheLoader, boolean async) {
      super(builder, cacheLoader, async);
      this.ticker = builder.getTicker();
      this.accessOrderWindowDeque = !builder.evicts() && !builder.expiresAfterAccess() ? null : new AccessOrderDeque();
      this.expiry = builder.getExpiry(this.isAsync);
      this.timerWheel = builder.expiresVariable() ? new TimerWheel(this) : null;
      this.expiresAfterAccessNanos = builder.getExpiresAfterAccessNanos();
      this.writeBuffer = new MpscGrowableArrayQueue<Runnable>(4, WRITE_BUFFER_MAX);
   }

   public final Ticker expirationTicker() {
      return this.ticker;
   }

   protected final AccessOrderDeque<Node<K, V>> accessOrderWindowDeque() {
      return this.accessOrderWindowDeque;
   }

   protected final boolean expiresVariable() {
      return this.timerWheel != null;
   }

   protected final Expiry<K, V> expiry() {
      return this.expiry;
   }

   protected final TimerWheel<K, V> timerWheel() {
      return this.timerWheel;
   }

   protected final boolean expiresAfterAccess() {
      return this.timerWheel == null;
   }

   protected final long expiresAfterAccessNanos() {
      return this.expiresAfterAccessNanos;
   }

   protected final void setExpiresAfterAccessNanos(long expiresAfterAccessNanos) {
      this.expiresAfterAccessNanos = expiresAfterAccessNanos;
   }

   protected final MpscGrowableArrayQueue<Runnable> writeBuffer() {
      return this.writeBuffer;
   }

   protected final boolean buffersWrites() {
      return true;
   }
}
