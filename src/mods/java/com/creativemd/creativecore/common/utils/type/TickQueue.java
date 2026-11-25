package com.creativemd.creativecore.common.utils.type;

import java.util.ArrayList;
import java.util.List;

public class TickQueue {
   private int tick;
   private int queueLength;
   private List<Runnable>[] queue;
   private TickQueue.LongQueue longQueue;
   private int index;

   public TickQueue(int queueLength) {
      this.setQueueLength(queueLength);
   }

   public void setQueueLength(int length) {
      List<Runnable>[] old = this.queue;
      int indexBefore = this.index;
      TickQueue.LongQueue oldLongQueue = this.longQueue;
      this.queue = new List[length];
      this.queueLength = length;

      for(int i = 0; i < this.queue.length; ++i) {
         this.queue[i] = new ArrayList();
      }

      this.longQueue = null;
      this.index = 0;
      if (old != null) {
         for(int i = 0; i < old.length; ++i) {
            this.queue(i + 1, old[(i + indexBefore) % old.length]);
         }

         for(TickQueue.LongQueue current = this.longQueue; current != null; current = current.next) {
            this.queue(current.enterTick - this.tick, current.run);
         }
      }

   }

   public void tick() {
      ++this.tick;
      List<Runnable> runs = this.queue[this.index];
      if (!runs.isEmpty()) {
         for(int i = 0; i < runs.size(); ++i) {
            ((Runnable)runs.get(i)).run();
         }
      }

      int lastIndex = this.index;
      this.index = (this.index + 1) % this.queueLength;

      TickQueue.LongQueue current;
      for(current = this.longQueue; current.enterTick <= this.tick; current = current.next) {
         this.queue[lastIndex].add(current.run);
         if (current.previous != null) {
            current.previous.next = null;
         }

         current.previous = null;
      }

      this.longQueue = current;
      this.longQueue.previous = null;
   }

   public void queue(int waitTicks, Runnable run) {
      if (waitTicks == 0) {
         run.run();
      } else if (waitTicks > this.queueLength) {
         this.queueLong(waitTicks, run);
      } else {
         int tick = waitTicks - 1;
         int index = (this.index + tick - 1) % this.queueLength;
         this.queue[index].add(run);
      }

   }

   public void queue(int waitTicks, List<Runnable> run) {
      if (waitTicks == 0) {
         for(int i = 0; i < run.size(); ++i) {
            ((Runnable)run.get(i)).run();
         }
      } else if (waitTicks > this.queueLength) {
         for(int i = 0; i < run.size(); ++i) {
            this.queueLong(waitTicks, (Runnable)run.get(i));
         }
      } else {
         int tick = waitTicks - 1;
         int index = (this.index + tick - 1) % this.queueLength;
         this.queue[index].addAll(run);
      }

   }

   protected void queueLong(int waitTicks, Runnable run) {
      TickQueue.LongQueue entry = new TickQueue.LongQueue(waitTicks, run);
      TickQueue.LongQueue previous = null;

      TickQueue.LongQueue current;
      for(current = this.longQueue; current != null && current.enterTick < entry.enterTick; current = current.next) {
         previous = current;
      }

      if (previous != null) {
         previous.next = entry;
      } else {
         this.longQueue = entry;
      }

      entry.previous = previous;
      entry.next = current;
      if (current != null) {
         current.previous = entry;
      }

   }

   class LongQueue {
      public final int enterTick;
      public final Runnable run;
      public TickQueue.LongQueue next;
      public TickQueue.LongQueue previous;

      public LongQueue(int waitTicks, Runnable run) {
         this.enterTick = TickQueue.this.tick + waitTicks;
         this.run = run;
      }
   }
}
