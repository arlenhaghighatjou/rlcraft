package com.github.benmanes.caffeine.cache;

import com.github.benmanes.caffeine.base.UnsafeAccess;

final class BBHeader {
   BBHeader() {
   }

   abstract static class PadReadCounter {
      long p00;
      long p01;
      long p02;
      long p03;
      long p04;
      long p05;
      long p06;
      long p07;
      long p10;
      long p11;
      long p12;
      long p13;
      long p14;
      long p15;
      long p16;

      PadReadCounter() {
      }
   }

   abstract static class PadWriteCounter extends BBHeader.ReadCounterRef {
      long p20;
      long p21;
      long p22;
      long p23;
      long p24;
      long p25;
      long p26;
      long p27;
      long p30;
      long p31;
      long p32;
      long p33;
      long p34;
      long p35;
      long p36;

      PadWriteCounter() {
      }
   }

   abstract static class ReadAndWriteCounterRef extends BBHeader.PadWriteCounter {
      static final long WRITE_OFFSET = UnsafeAccess.objectFieldOffset(BBHeader.ReadAndWriteCounterRef.class, "writeCounter");
      volatile long writeCounter;

      ReadAndWriteCounterRef() {
         UnsafeAccess.UNSAFE.putOrderedLong(this, WRITE_OFFSET, 16L);
      }

      long relaxedWriteCounter() {
         return UnsafeAccess.UNSAFE.getLong(this, WRITE_OFFSET);
      }

      boolean casWriteCounter(long expect, long update) {
         return UnsafeAccess.UNSAFE.compareAndSwapLong(this, WRITE_OFFSET, expect, update);
      }
   }

   abstract static class ReadCounterRef extends BBHeader.PadReadCounter {
      static final long READ_OFFSET = UnsafeAccess.objectFieldOffset(BBHeader.ReadCounterRef.class, "readCounter");
      volatile long readCounter;

      ReadCounterRef() {
      }

      void lazySetReadCounter(long count) {
         UnsafeAccess.UNSAFE.putOrderedLong(this, READ_OFFSET, count);
      }
   }
}
