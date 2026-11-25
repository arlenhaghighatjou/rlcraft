package com.github.benmanes.caffeine.cache;

abstract class MpscChunkedArrayQueueColdProducerFields<E> extends BaseMpscLinkedArrayQueue<E> {
   protected final long maxQueueCapacity;

   public MpscChunkedArrayQueueColdProducerFields(int initialCapacity, int maxCapacity) {
      super(initialCapacity);
      if (maxCapacity < 4) {
         throw new IllegalArgumentException("Max capacity must be 4 or more");
      } else if (BoundedLocalCache.ceilingPowerOfTwo(initialCapacity) >= BoundedLocalCache.ceilingPowerOfTwo(maxCapacity)) {
         throw new IllegalArgumentException("Initial capacity cannot exceed maximum capacity(both rounded up to a power of 2)");
      } else {
         this.maxQueueCapacity = (long)BoundedLocalCache.ceilingPowerOfTwo(maxCapacity) << 1;
      }
   }
}
