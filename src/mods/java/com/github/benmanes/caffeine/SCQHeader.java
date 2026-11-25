package com.github.benmanes.caffeine;

import com.github.benmanes.caffeine.base.UnsafeAccess;
import java.util.AbstractQueue;
import org.checkerframework.checker.nullness.qual.Nullable;

final class SCQHeader {
   SCQHeader() {
   }

   abstract static class HeadAndTailRef<E> extends SCQHeader.PadHeadAndTail<E> {
      static final long TAIL_OFFSET = UnsafeAccess.objectFieldOffset(SCQHeader.HeadAndTailRef.class, "tail");
      @Nullable
      volatile SingleConsumerQueue.Node<E> tail;

      HeadAndTailRef() {
      }

      void lazySetTail(SingleConsumerQueue.Node<E> next) {
         UnsafeAccess.UNSAFE.putOrderedObject(this, TAIL_OFFSET, next);
      }

      boolean casTail(SingleConsumerQueue.Node<E> expect, SingleConsumerQueue.Node<E> update) {
         return UnsafeAccess.UNSAFE.compareAndSwapObject(this, TAIL_OFFSET, expect, update);
      }
   }

   abstract static class HeadRef<E> extends SCQHeader.PadHead<E> {
      @Nullable
      SingleConsumerQueue.Node<E> head;

      HeadRef() {
      }
   }

   abstract static class PadHead<E> extends AbstractQueue<E> {
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

      PadHead() {
      }
   }

   abstract static class PadHeadAndTail<E> extends SCQHeader.HeadRef<E> {
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

      PadHeadAndTail() {
      }
   }
}
