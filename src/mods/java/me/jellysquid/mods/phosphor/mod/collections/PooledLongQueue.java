package me.jellysquid.mods.phosphor.mod.collections;

import java.util.ArrayDeque;
import java.util.Deque;

public class PooledLongQueue {
   private static final int CACHED_QUEUE_SEGMENTS_COUNT = 4096;
   private static final int QUEUE_SEGMENT_SIZE = 1024;
   private final PooledLongQueue.Pool pool;
   private PooledLongQueue.Segment cur;
   private PooledLongQueue.Segment last;
   private int size = 0;
   private volatile boolean empty;

   public PooledLongQueue(PooledLongQueue.Pool pool) {
      this.pool = pool;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.empty;
   }

   public void add(long val) {
      if (this.cur == null) {
         this.empty = false;
         this.cur = this.last = this.pool.acquire();
      }

      if (this.last.index == 1024) {
         PooledLongQueue.Segment ret = this.last.next = this.last.pool.acquire();
         ret.longArray[ret.index++] = val;
         this.last = ret;
      } else {
         this.last.longArray[this.last.index++] = val;
      }

      ++this.size;
   }

   public PooledLongQueue.LongQueueIterator iterator() {
      return new PooledLongQueue.LongQueueIterator(this.cur);
   }

   private void clear() {
      PooledLongQueue.Segment next;
      for(PooledLongQueue.Segment segment = this.cur; segment != null; segment = next) {
         next = segment.next;
         segment.release();
      }

      this.size = 0;
      this.cur = null;
      this.last = null;
      this.empty = true;
   }

   public class LongQueueIterator {
      private PooledLongQueue.Segment cur;
      private long[] curArray;
      private int index;
      private int capacity;

      private LongQueueIterator(PooledLongQueue.Segment cur) {
         this.cur = cur;
         if (this.cur != null) {
            this.curArray = cur.longArray;
            this.capacity = cur.index;
         }

      }

      public boolean hasNext() {
         return this.cur != null;
      }

      public long next() {
         long ret = this.curArray[this.index++];
         if (this.index == this.capacity) {
            this.index = 0;
            this.cur = this.cur.next;
            if (this.cur != null) {
               this.curArray = this.cur.longArray;
               this.capacity = this.cur.index;
            }
         }

         return ret;
      }

      public void finish() {
         PooledLongQueue.this.clear();
      }
   }

   public static class Pool {
      private final Deque<PooledLongQueue.Segment> segmentPool = new ArrayDeque();

      public Pool() {
      }

      private PooledLongQueue.Segment acquire() {
         return this.segmentPool.isEmpty() ? new PooledLongQueue.Segment(this) : (PooledLongQueue.Segment)this.segmentPool.pop();
      }

      private void release(PooledLongQueue.Segment segment) {
         if (this.segmentPool.size() < 4096) {
            this.segmentPool.push(segment);
         }

      }
   }

   private static class Segment {
      private final long[] longArray;
      private int index;
      private PooledLongQueue.Segment next;
      private final PooledLongQueue.Pool pool;

      private Segment(PooledLongQueue.Pool pool) {
         this.longArray = new long[1024];
         this.index = 0;
         this.pool = pool;
      }

      private void release() {
         this.index = 0;
         this.next = null;
         this.pool.release(this);
      }
   }
}
