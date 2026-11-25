package slimeknights.mantle.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.UnmodifiableListIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;

public class ImmutableConcatList<E> implements List<E> {
   protected final FluentIterable<E> iterable;

   public ImmutableConcatList(List<E>... lists) {
      this.iterable = FluentIterable.from(Iterables.concat(lists));
   }

   public ImmutableConcatList(List<List<E>> lists) {
      this.iterable = FluentIterable.from(Iterables.concat(lists));
   }

   public int size() {
      return this.iterable.size();
   }

   public boolean isEmpty() {
      return this.iterable.isEmpty();
   }

   public boolean contains(Object o) {
      return this.iterable.contains(o);
   }

   @Nonnull
   public Iterator<E> iterator() {
      return this.iterable.iterator();
   }

   @Nonnull
   public Object[] toArray() {
      return this.iterable.toList().toArray();
   }

   @Nonnull
   public <T> T[] toArray(@Nonnull T[] a) {
      return (T[])this.iterable.toList().toArray(a);
   }

   public boolean add(E e) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(Object o) {
      throw new UnsupportedOperationException();
   }

   public boolean containsAll(@Nonnull Collection<?> c) {
      for(Object e : c) {
         if (!this.contains(e)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(@Nonnull Collection<? extends E> c) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int index, @Nonnull Collection<? extends E> c) {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(@Nonnull Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(@Nonnull Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public E get(int index) {
      return (E)this.iterable.get(index);
   }

   public E set(int index, E element) {
      throw new UnsupportedOperationException();
   }

   public void add(int index, E element) {
      throw new UnsupportedOperationException();
   }

   public E remove(int index) {
      throw new UnsupportedOperationException();
   }

   public int indexOf(Object o) {
      Iterator<E> iterator = this.iterable.iterator();

      for(int i = 0; iterator.hasNext(); ++i) {
         if (o == iterator.next()) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexOf(Object o) {
      Iterator<E> iterator = this.iterable.iterator();
      int j = -1;

      for(int i = 0; iterator.hasNext(); ++i) {
         if (o == iterator.next()) {
            j = i;
         }
      }

      return j;
   }

   @Nonnull
   public ListIterator<E> listIterator() {
      return this.listIterator(0);
   }

   @Nonnull
   public ListIterator<E> listIterator(int index) {
      return new ImmutableConcatList.ListItr<E>(this.size(), index) {
         protected E get(int index) {
            return (E)ImmutableConcatList.this.get(index);
         }
      };
   }

   @Nonnull
   public List<E> subList(int fromIndex, int toIndex) {
      throw new UnsupportedOperationException();
   }

   private abstract static class ListItr<E> extends UnmodifiableListIterator<E> {
      private final int size;
      private int position;

      public ListItr(int size, int position) {
         Preconditions.checkPositionIndex(position, size);
         this.size = size;
         this.position = position;
      }

      protected abstract E get(int var1);

      public final boolean hasNext() {
         return this.position < this.size;
      }

      public final E next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return (E)this.get(this.position++);
         }
      }

      public final int nextIndex() {
         return this.position;
      }

      public final boolean hasPrevious() {
         return this.position > 0;
      }

      public final E previous() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return (E)this.get(--this.position);
         }
      }

      public final int previousIndex() {
         return this.position - 1;
      }
   }
}
