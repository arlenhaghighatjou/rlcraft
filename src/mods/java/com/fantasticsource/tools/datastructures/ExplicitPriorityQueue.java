package com.fantasticsource.tools.datastructures;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.function.Predicate;

public class ExplicitPriorityQueue<T> {
   private PriorityQueue<ExplicitPriorityQueue.Entry> queue;

   public ExplicitPriorityQueue(int initialSize) {
      this.queue = new PriorityQueue(Math.max(1, initialSize));
   }

   public ExplicitPriorityQueue() {
      this(11);
   }

   public void add(T object, double priority) {
      this.queue.add(new ExplicitPriorityQueue.Entry(object, priority));
   }

   public boolean removeAll(T object) {
      return this.queue.removeIf((e) -> {
         return e.object == object;
      });
   }

   public boolean removeIf(Predicate<ExplicitPriorityQueue.Entry> predicate) {
      return this.queue.removeIf(predicate);
   }

   public T peek() {
      ExplicitPriorityQueue<T>.Entry<T> result = (ExplicitPriorityQueue.Entry)this.queue.peek();
      return (T)(result == null ? null : result.object);
   }

   public double peekPriority() {
      ExplicitPriorityQueue<T>.Entry<T> result = (ExplicitPriorityQueue.Entry)this.queue.peek();
      return result == null ? Double.NaN : result.priority;
   }

   public T poll() {
      ExplicitPriorityQueue<T>.Entry<T> result = (ExplicitPriorityQueue.Entry)this.queue.poll();
      return (T)(result == null ? null : result.object);
   }

   public T[] toArray(T[] a) {
      Object[] internal = this.toArray();
      if (a.length < internal.length) {
         return (T[])(Arrays.copyOf(this.toArray(), internal.length, a.getClass()));
      } else {
         System.arraycopy(internal, 0, a, 0, internal.length);
         return a;
      }
   }

   public Object[] toArray() {
      Object[] result = new Object[this.queue.size()];
      ExplicitPriorityQueue<T> clone = this.clone();

      for(int i = 0; i < result.length; ++i) {
         result[i] = clone.poll();
      }

      return result;
   }

   public void clear() {
      this.queue.clear();
   }

   public int size() {
      return this.queue.size();
   }

   public boolean isEmpty() {
      return this.queue.isEmpty();
   }

   public ExplicitPriorityQueue<T> clone() {
      ExplicitPriorityQueue<T> clone = new ExplicitPriorityQueue<T>(this.size());
      ExplicitPriorityQueue<T>.Entry<T>[] array = (ExplicitPriorityQueue.Entry[])this.queue.toArray(new ExplicitPriorityQueue.Entry[this.size()]);

      for(ExplicitPriorityQueue<T>.Entry<T> entry : array) {
         clone.add(entry);
      }

      return clone;
   }

   private void add(ExplicitPriorityQueue<T>.Entry<T> entry) {
      this.queue.add(entry);
   }

   public class Entry<A> implements Comparable<ExplicitPriorityQueue.Entry> {
      public A object;
      public double priority;

      Entry(A object, double priority) {
         this.object = object;
         this.priority = priority;
      }

      public int compareTo(ExplicitPriorityQueue.Entry o) {
         return Double.compare(this.priority, o.priority);
      }
   }
}
