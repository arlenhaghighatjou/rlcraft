package com.github.benmanes.caffeine.cache;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class TimerWheel<K, V> {
   static final int[] BUCKETS = new int[]{64, 64, 32, 4, 1};
   static final long[] SPANS = new long[]{ceilingPowerOfTwo(TimeUnit.SECONDS.toNanos(1L)), ceilingPowerOfTwo(TimeUnit.MINUTES.toNanos(1L)), ceilingPowerOfTwo(TimeUnit.HOURS.toNanos(1L)), ceilingPowerOfTwo(TimeUnit.DAYS.toNanos(1L)), (long)BUCKETS[3] * ceilingPowerOfTwo(TimeUnit.DAYS.toNanos(1L)), (long)BUCKETS[3] * ceilingPowerOfTwo(TimeUnit.DAYS.toNanos(1L))};
   static final long[] SHIFT = new long[]{(long)(64 - Long.numberOfLeadingZeros(SPANS[0] - 1L)), (long)(64 - Long.numberOfLeadingZeros(SPANS[1] - 1L)), (long)(64 - Long.numberOfLeadingZeros(SPANS[2] - 1L)), (long)(64 - Long.numberOfLeadingZeros(SPANS[3] - 1L)), (long)(64 - Long.numberOfLeadingZeros(SPANS[4] - 1L))};
   final BoundedLocalCache<K, V> cache;
   final Node<K, V>[][] wheel;
   long nanos;

   TimerWheel(BoundedLocalCache<K, V> cache) {
      this.cache = (BoundedLocalCache)Objects.requireNonNull(cache);
      this.wheel = new Node[BUCKETS.length][1];

      for(int i = 0; i < this.wheel.length; ++i) {
         this.wheel[i] = new Node[BUCKETS[i]];

         for(int j = 0; j < this.wheel[i].length; ++j) {
            this.wheel[i][j] = new TimerWheel.Sentinel();
         }
      }

   }

   public void advance(long currentTimeNanos) {
      long previousTimeNanos = this.nanos;

      try {
         this.nanos = currentTimeNanos;

         for(int i = 0; i < SHIFT.length; ++i) {
            long previousTicks = previousTimeNanos >> (int)SHIFT[i];
            long currentTicks = currentTimeNanos >> (int)SHIFT[i];
            if (currentTicks - previousTicks <= 0L) {
               break;
            }

            this.expire(i, previousTicks, currentTicks);
         }

      } catch (Throwable var10) {
         this.nanos = previousTimeNanos;
         throw var10;
      }
   }

   void expire(int index, long previousTicks, long currentTicks) {
      Node<K, V>[] timerWheel = this.wheel[index];
      int start;
      int end;
      if (currentTicks - previousTicks >= (long)timerWheel.length) {
         end = timerWheel.length;
         start = 0;
      } else {
         long mask = SPANS[index] - 1L;
         start = (int)(previousTicks & mask);
         end = 1 + (int)(currentTicks & mask);
      }

      int mask = timerWheel.length - 1;

      for(int i = start; i < end; ++i) {
         Node<K, V> sentinel = timerWheel[i & mask];
         Node<K, V> prev = sentinel.getPreviousInVariableOrder();
         Node<K, V> node = sentinel.getNextInVariableOrder();
         sentinel.setPreviousInVariableOrder(sentinel);
         sentinel.setNextInVariableOrder(sentinel);

         while(node != sentinel) {
            Node<K, V> next = node.getNextInVariableOrder();
            node.setPreviousInVariableOrder((Node)null);
            node.setNextInVariableOrder((Node)null);

            try {
               if (node.getVariableTime() - this.nanos > 0L || !this.cache.evictEntry(node, RemovalCause.EXPIRED, this.nanos)) {
                  Node<K, V> newSentinel = this.findBucket(node.getVariableTime());
                  this.link(newSentinel, node);
               }

               node = next;
            } catch (Throwable var16) {
               node.setPreviousInVariableOrder(sentinel.getPreviousInVariableOrder());
               node.setNextInVariableOrder(next);
               sentinel.getPreviousInVariableOrder().setNextInVariableOrder(node);
               sentinel.setPreviousInVariableOrder(prev);
               throw var16;
            }
         }
      }

   }

   public void schedule(@NonNull Node<K, V> node) {
      Node<K, V> sentinel = this.findBucket(node.getVariableTime());
      this.link(sentinel, node);
   }

   public void reschedule(@NonNull Node<K, V> node) {
      if (node.getNextInVariableOrder() != null) {
         this.unlink(node);
         this.schedule(node);
      }

   }

   public void deschedule(@NonNull Node<K, V> node) {
      this.unlink(node);
      node.setNextInVariableOrder((Node)null);
      node.setPreviousInVariableOrder((Node)null);
   }

   Node<K, V> findBucket(long time) {
      long duration = time - this.nanos;
      int length = this.wheel.length - 1;

      for(int i = 0; i < length; ++i) {
         if (duration < SPANS[i + 1]) {
            int ticks = (int)(time >> (int)SHIFT[i]);
            int index = ticks & this.wheel[i].length - 1;
            return this.wheel[i][index];
         }
      }

      return this.wheel[length][0];
   }

   void link(Node<K, V> sentinel, Node<K, V> node) {
      node.setPreviousInVariableOrder(sentinel.getPreviousInVariableOrder());
      node.setNextInVariableOrder(sentinel);
      sentinel.getPreviousInVariableOrder().setNextInVariableOrder(node);
      sentinel.setPreviousInVariableOrder(node);
   }

   void unlink(Node<K, V> node) {
      Node<K, V> next = node.getNextInVariableOrder();
      if (next != null) {
         Node<K, V> prev = node.getPreviousInVariableOrder();
         next.setPreviousInVariableOrder(prev);
         prev.setNextInVariableOrder(next);
      }

   }

   public Map<K, V> snapshot(boolean ascending, int limit, @NonNull Function<V, V> transformer) {
      Caffeine.requireArgument(limit >= 0);
      Map<K, V> map = new LinkedHashMap(Math.min(limit, this.cache.size()));
      int startLevel = ascending ? 0 : this.wheel.length - 1;

      for(int i = 0; i < this.wheel.length; ++i) {
         int indexOffset = ascending ? i : -i;
         int index = startLevel + indexOffset;
         int ticks = (int)(this.nanos >> (int)SHIFT[index]);
         int bucketMask = this.wheel[index].length - 1;
         int startBucket = (ticks & bucketMask) + (ascending ? 1 : 0);

         for(int j = 0; j < this.wheel[index].length; ++j) {
            int bucketOffset = ascending ? j : -j;
            Node<K, V> sentinel = this.wheel[index][startBucket + bucketOffset & bucketMask];

            for(Node<K, V> node = traverse(ascending, sentinel); node != sentinel && map.size() < limit; node = traverse(ascending, node)) {
               K key = node.getKey();
               V value = (V)transformer.apply(node.getValue());
               if (key != null && value != null && node.isAlive()) {
                  map.put(key, value);
               }
            }
         }
      }

      return Collections.unmodifiableMap(map);
   }

   static <K, V> Node<K, V> traverse(boolean ascending, Node<K, V> node) {
      return ascending ? node.getNextInVariableOrder() : node.getPreviousInVariableOrder();
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();

      for(int i = 0; i < this.wheel.length; ++i) {
         Map<Integer, List<K>> buckets = new TreeMap();

         for(int j = 0; j < this.wheel[i].length; ++j) {
            List<K> events = new ArrayList();

            for(Node<K, V> node = this.wheel[i][j].getNextInVariableOrder(); node != this.wheel[i][j]; node = node.getNextInVariableOrder()) {
               events.add(node.getKey());
            }

            if (!events.isEmpty()) {
               buckets.put(Integer.valueOf(j), events);
            }
         }

         builder.append("Wheel #").append(i + 1).append(": ").append(buckets).append('\n');
      }

      return builder.deleteCharAt(builder.length() - 1).toString();
   }

   static long ceilingPowerOfTwo(long x) {
      return 1L << -Long.numberOfLeadingZeros(x - 1L);
   }

   static final class Sentinel<K, V> extends Node<K, V> {
      Node<K, V> prev;
      Node<K, V> next;

      Sentinel() {
         this.prev = this.next = this;
      }

      public Node<K, V> getPreviousInVariableOrder() {
         return this.prev;
      }

      public void setPreviousInVariableOrder(@Nullable Node<K, V> prev) {
         this.prev = prev;
      }

      public Node<K, V> getNextInVariableOrder() {
         return this.next;
      }

      public void setNextInVariableOrder(@Nullable Node<K, V> next) {
         this.next = next;
      }

      @Nullable
      public K getKey() {
         return (K)null;
      }

      public Object getKeyReference() {
         throw new UnsupportedOperationException();
      }

      @Nullable
      public V getValue() {
         return (V)null;
      }

      public Object getValueReference() {
         throw new UnsupportedOperationException();
      }

      public void setValue(V value, @Nullable ReferenceQueue<V> referenceQueue) {
      }

      public boolean containsValue(Object value) {
         return false;
      }

      public boolean isAlive() {
         return false;
      }

      public boolean isRetired() {
         return false;
      }

      public boolean isDead() {
         return false;
      }

      public void retire() {
      }

      public void die() {
      }
   }
}
