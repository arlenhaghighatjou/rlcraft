package org.orecruncher.lib.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class ObjectArray<T> implements Collection<T> {
   private static final int DEFAULT_SIZE = 16;
   protected Object[] data;
   protected int insertionIdx;

   public ObjectArray() {
      this(16);
   }

   public ObjectArray(int size) {
      this.data = new Object[size];
   }

   private void resize() {
      Object[] t = new Object[this.data.length * 2];
      System.arraycopy(this.data, 0, t, 0, this.data.length);
      this.data = t;
   }

   public int size() {
      return this.insertionIdx;
   }

   public T get(int idx) {
      return (T)(idx >= 0 && idx < this.insertionIdx ? this.data[idx] : null);
   }

   private void remove0(int idx) {
      Object m = this.data[--this.insertionIdx];
      this.data[this.insertionIdx] = null;
      if (idx < this.insertionIdx) {
         this.data[idx] = m;
      }

   }

   public boolean removeIf(@Nonnull Predicate<? super T> pred) {
      boolean result = false;

      for(int i = this.insertionIdx - 1; i >= 0; --i) {
         if (pred.test(this.data[i])) {
            result = true;
            this.remove0(i);
         }
      }

      return result;
   }

   public void forEach(@Nonnull Consumer<? super T> consumer) {
      for(int i = 0; i < this.insertionIdx; ++i) {
         consumer.accept(this.data[i]);
      }

   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   private int find(@Nonnull Object o) {
      for(int i = 0; i < this.insertionIdx; ++i) {
         if (o.equals(this.data[i])) {
            return i;
         }
      }

      return -1;
   }

   public boolean contains(@Nonnull Object o) {
      return this.find(o) != -1;
   }

   @Nonnull
   public Object[] toArray() {
      Object[] result = new Object[this.insertionIdx];
      System.arraycopy(this.data, 0, result, 0, this.insertionIdx);
      return result;
   }

   @Nonnull
   public <T> T[] toArray(@Nonnull T[] a) {
      if (a.length < this.insertionIdx) {
         return (T[])(Arrays.copyOf(this.data, this.insertionIdx, a.getClass()));
      } else {
         System.arraycopy(this.data, 0, a, 0, this.insertionIdx);
         if (a.length > this.insertionIdx) {
            a[this.insertionIdx] = null;
         }

         return a;
      }
   }

   public boolean add(@Nonnull T e) {
      if (e == null) {
         return false;
      } else {
         if (this.data.length == this.insertionIdx) {
            this.resize();
         }

         this.data[this.insertionIdx++] = e;
         return true;
      }
   }

   public boolean remove(@Nonnull Object o) {
      int idx = this.find(o);
      if (idx != -1) {
         this.remove0(idx);
      }

      return idx != -1;
   }

   public boolean containsAll(@Nonnull Collection<?> c) {
      for(Object obj : c) {
         if (!this.contains(obj)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(@Nonnull Collection<? extends T> c) {
      boolean result = false;

      for(T element : c) {
         result |= this.add(element);
      }

      return result;
   }

   public boolean addAll(@Nonnull T[] list) {
      boolean result = false;

      for(int i = 0; i < list.length; ++i) {
         result |= this.add(list[i]);
      }

      return result;
   }

   public boolean removeAll(@Nonnull Collection<?> c) {
      return this.removeIf((entry) -> {
         return c.contains(entry);
      });
   }

   public boolean retainAll(@Nonnull Collection<?> c) {
      return this.removeIf((entry) -> {
         return !c.contains(entry);
      });
   }

   public void clear() {
      for(int i = 0; i < this.insertionIdx; ++i) {
         this.data[i] = null;
      }

      this.insertionIdx = 0;
   }

   @Nonnull
   public Iterator<T> iterator() {
      return new Iterator<T>() {
         private int idx = -1;

         public boolean hasNext() {
            return this.idx + 1 < ObjectArray.this.insertionIdx;
         }

         public T next() {
            return (T)ObjectArray.this.data[++this.idx];
         }
      };
   }
}
