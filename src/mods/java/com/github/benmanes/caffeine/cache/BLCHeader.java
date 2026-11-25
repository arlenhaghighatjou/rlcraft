package com.github.benmanes.caffeine.cache;

import com.github.benmanes.caffeine.base.UnsafeAccess;
import java.util.AbstractMap;

final class BLCHeader {
   BLCHeader() {
   }

   abstract static class DrainStatusRef<K, V> extends BLCHeader.PadDrainStatus<K, V> {
      static final long DRAIN_STATUS_OFFSET = UnsafeAccess.objectFieldOffset(BLCHeader.DrainStatusRef.class, "drainStatus");
      static final int IDLE = 0;
      static final int REQUIRED = 1;
      static final int PROCESSING_TO_IDLE = 2;
      static final int PROCESSING_TO_REQUIRED = 3;
      volatile int drainStatus = 0;

      DrainStatusRef() {
      }

      boolean shouldDrainBuffers(boolean delayable) {
         switch(this.drainStatus()) {
         case 0:
            return !delayable;
         case 1:
            return true;
         case 2:
         case 3:
            return false;
         default:
            throw new IllegalStateException();
         }
      }

      int drainStatus() {
         return UnsafeAccess.UNSAFE.getInt(this, DRAIN_STATUS_OFFSET);
      }

      void lazySetDrainStatus(int drainStatus) {
         UnsafeAccess.UNSAFE.putOrderedInt(this, DRAIN_STATUS_OFFSET, drainStatus);
      }

      boolean casDrainStatus(int expect, int update) {
         return UnsafeAccess.UNSAFE.compareAndSwapInt(this, DRAIN_STATUS_OFFSET, expect, update);
      }
   }

   abstract static class PadDrainStatus<K, V> extends AbstractMap<K, V> {
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

      PadDrainStatus() {
      }
   }
}
