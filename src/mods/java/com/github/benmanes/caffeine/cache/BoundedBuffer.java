package com.github.benmanes.caffeine.cache;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Consumer;

final class BoundedBuffer<E> extends StripedBuffer<E> {
   static final int BUFFER_SIZE = 16;
   static final int SPACED_SIZE = 256;
   static final int SPACED_MASK = 255;
   static final int OFFSET = 16;

   BoundedBuffer() {
   }

   protected Buffer<E> create(E e) {
      return new BoundedBuffer.RingBuffer<E>(e);
   }

   static final class RingBuffer<E> extends BBHeader.ReadAndWriteCounterRef implements Buffer<E> {
      final AtomicReferenceArray<E> buffer = new AtomicReferenceArray(256);

      public RingBuffer(E e) {
         this.buffer.lazySet(0, e);
      }

      public int offer(E e) {
         long head = this.readCounter;
         long tail = this.relaxedWriteCounter();
         long size = tail - head;
         if (size >= 256L) {
            return 1;
         } else if (this.casWriteCounter(tail, tail + 16L)) {
            int index = (int)(tail & 255L);
            this.buffer.lazySet(index, e);
            return 0;
         } else {
            return -1;
         }
      }

      public void drainTo(Consumer<E> consumer) {
         long head = this.readCounter;
         long tail = this.relaxedWriteCounter();
         long size = tail - head;
         if (size != 0L) {
            while(true) {
               int index = (int)(head & 255L);
               E e = (E)this.buffer.get(index);
               if (e == null) {
                  break;
               }

               this.buffer.lazySet(index, (Object)null);
               consumer.accept(e);
               head += 16L;
               if (head == tail) {
                  break;
               }
            }

            this.lazySetReadCounter(head);
         }
      }

      public int reads() {
         return (int)this.readCounter / 16;
      }

      public int writes() {
         return (int)this.writeCounter / 16;
      }
   }
}
