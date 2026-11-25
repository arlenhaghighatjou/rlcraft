package org.orecruncher.lib.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;

public class EmptySet<T> implements Set<T> {
   private static final EmptySet<?> EMPTY = new EmptySet();

   public EmptySet() {
   }

   public static <T> EmptySet<T> empty() {
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

   public boolean add(@Nonnull T e) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public boolean remove(@Nonnull Object o) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public boolean containsAll(@Nonnull Collection<?> c) {
      return false;
   }

   public boolean addAll(@Nonnull Collection<? extends T> c) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public boolean retainAll(@Nonnull Collection<?> c) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public boolean removeAll(Collection<?> c) {
      throw new IllegalStateException("Set is EMPTY");
   }

   public void clear() {
   }
}
