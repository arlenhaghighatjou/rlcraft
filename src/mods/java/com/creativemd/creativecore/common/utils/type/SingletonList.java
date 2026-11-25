package com.creativemd.creativecore.common.utils.type;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class SingletonList<E> extends AbstractList<E> implements RandomAccess, Serializable {
   private static final long serialVersionUID = 3093736618740652951L;
   private E element;

   public SingletonList(E obj) {
      this.element = obj;
   }

   public SingletonList<E> setElement(E element) {
      this.element = element;
      return this;
   }

   public Iterator<E> iterator() {
      return new Iterator<E>() {
         private boolean hasNext = true;

         public boolean hasNext() {
            return this.hasNext;
         }

         public E next() {
            if (this.hasNext) {
               this.hasNext = false;
               return SingletonList.this.element;
            } else {
               throw new NoSuchElementException();
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }

         public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            if (this.hasNext) {
               action.accept(SingletonList.this.element);
               this.hasNext = false;
            }

         }
      };
   }

   public int size() {
      return 1;
   }

   public boolean contains(Object obj) {
      return this.element == null ? obj == null : this.element.equals(obj);
   }

   public E get(int index) {
      if (index != 0) {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");
      } else {
         return this.element;
      }
   }

   public void forEach(Consumer<? super E> action) {
      action.accept(this.element);
   }

   public boolean removeIf(Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
   }

   public void replaceAll(UnaryOperator<E> operator) {
      throw new UnsupportedOperationException();
   }

   public void sort(Comparator<? super E> c) {
   }

   public Spliterator<E> spliterator() {
      return new Spliterator<E>() {
         long est = 1L;

         public Spliterator<E> trySplit() {
            return null;
         }

         public boolean tryAdvance(Consumer<? super E> consumer) {
            Objects.requireNonNull(consumer);
            if (this.est > 0L) {
               --this.est;
               consumer.accept(SingletonList.this.element);
               return true;
            } else {
               return false;
            }
         }

         public void forEachRemaining(Consumer<? super E> consumer) {
            this.tryAdvance(consumer);
         }

         public long estimateSize() {
            return this.est;
         }

         public int characteristics() {
            int value = SingletonList.this.element != null ? 256 : 0;
            return value | 64 | 16384 | 1024 | 1 | 16;
         }
      };
   }
}
