package org.orecruncher.lib.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EmptyList<T> implements List<T> {
   private static final EmptyList<?> EMPTY = new EmptyList();

   public EmptyList() {
   }

   public static <T> EmptyList<T> empty() {
      return EMPTY;
   }

   public int size() {
      return 0;
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean contains(@Nonnull Object o) {
      return false;
   }

   public Iterator<T> iterator() {
      return new Iterator<T>() {
         public boolean hasNext() {
            return false;
         }

         public T next() {
            throw new IllegalStateException("Set is EMPTY");
         }
      };
   }

   public Object[] toArray() {
      return new Object[0];
   }

   public <T> T[] toArray(T[] a) {
      return (T[])(new Object[0]);
   }

   public boolean add(@Nullable T e) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public boolean remove(@Nullable Object o) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public boolean containsAll(@Nonnull Collection<?> c) {
      return false;
   }

   public boolean addAll(@Nonnull Collection<? extends T> c) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public boolean addAll(int index, @Nonnull Collection<? extends T> c) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public boolean removeAll(@Nonnull Collection<?> c) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public boolean retainAll(@Nonnull Collection<?> c) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public void clear() {
   }

   public T get(int index) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public T set(int index, @Nonnull T element) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public void add(int index, @Nonnull T element) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public T remove(int index) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public int indexOf(@Nonnull Object o) {
      return -1;
   }

   public int lastIndexOf(@Nonnull Object o) {
      return -1;
   }

   public ListIterator<T> listIterator() {
      return null;
   }

   public ListIterator<T> listIterator(int index) {
      return new ListIterator<T>() {
         public boolean hasNext() {
            return false;
         }

         public T next() {
            throw new IllegalStateException("Set is EMPTY");
         }

         public boolean hasPrevious() {
            return false;
         }

         public T previous() {
            throw new IllegalStateException("Set is EMPTY");
         }

         public int nextIndex() {
            throw new IllegalStateException("Set is EMPTY");
         }

         public int previousIndex() {
            throw new IllegalStateException("Set is EMPTY");
         }

         public void remove() {
            throw new IllegalStateException("Set is EMPTY");
         }

         public void set(T e) {
            throw new IllegalStateException("Set is EMPTY");
         }

         public void add(T e) {
            throw new IllegalStateException("Set is EMPTY");
         }
      };
   }

   public List<T> subList(int fromIndex, int toIndex) {
      throw new IllegalStateException("Set is EMPTY");
   }
}
