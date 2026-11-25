package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

class PotentiallyUnmodifiableList<T> extends ArrayList<T> {
   static final long serialVersionUID = 1L;
   boolean modifiable = true;

   PotentiallyUnmodifiableList() {
   }

   PotentiallyUnmodifiableList(int sizeHint) {
      super(sizeHint);
   }

   PotentiallyUnmodifiableList(Collection<T> collection) {
      super(collection);
   }

   public boolean equals(Object o) {
      return super.equals(o);
   }

   public int hashCode() {
      return super.hashCode();
   }

   void makeUnmodifiable() {
      this.modifiable = false;
   }

   public boolean add(T element) {
      if (!this.modifiable) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         return super.add(element);
      }
   }

   public void add(int index, T element) {
      if (!this.modifiable) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         super.add(index, element);
      }
   }

   public boolean remove(Object o) {
      if (!this.modifiable) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         return super.remove(o);
      }
   }

   public T remove(int index) {
      if (!this.modifiable) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         return (T)super.remove(index);
      }
   }

   public boolean addAll(Collection<? extends T> c) {
      if (!this.modifiable && !c.isEmpty()) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         return super.addAll(c);
      }
   }

   public boolean addAll(int index, Collection<? extends T> c) {
      if (!this.modifiable && !c.isEmpty()) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         return super.addAll(index, c);
      }
   }

   public boolean removeAll(Collection<?> c) {
      if (!this.modifiable && !c.isEmpty()) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         return super.removeAll(c);
      }
   }

   public boolean retainAll(Collection<?> c) {
      if (!this.modifiable && !this.isEmpty()) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         return super.retainAll(c);
      }
   }

   public void clear() {
      if (!this.modifiable && !this.isEmpty()) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         super.clear();
      }
   }

   public T set(int index, T element) {
      if (!this.modifiable) {
         throw new IllegalArgumentException("List is immutable");
      } else {
         return (T)super.set(index, element);
      }
   }

   public Iterator<T> iterator() {
      final Iterator<T> iterator = super.iterator();
      return new Iterator<T>() {
         public boolean hasNext() {
            return PotentiallyUnmodifiableList.this.isEmpty() ? false : iterator.hasNext();
         }

         public T next() {
            return (T)iterator.next();
         }

         public void remove() {
            if (!PotentiallyUnmodifiableList.this.modifiable) {
               throw new IllegalArgumentException("List is immutable");
            } else {
               iterator.remove();
            }
         }
      };
   }

   public ListIterator<T> listIterator() {
      final ListIterator<T> iterator = super.listIterator();
      return new ListIterator<T>() {
         public boolean hasNext() {
            return PotentiallyUnmodifiableList.this.isEmpty() ? false : iterator.hasNext();
         }

         public T next() {
            return (T)iterator.next();
         }

         public boolean hasPrevious() {
            return PotentiallyUnmodifiableList.this.isEmpty() ? false : iterator.hasPrevious();
         }

         public T previous() {
            return (T)iterator.previous();
         }

         public int nextIndex() {
            return PotentiallyUnmodifiableList.this.isEmpty() ? 0 : iterator.nextIndex();
         }

         public int previousIndex() {
            return PotentiallyUnmodifiableList.this.isEmpty() ? -1 : iterator.previousIndex();
         }

         public void remove() {
            if (!PotentiallyUnmodifiableList.this.modifiable) {
               throw new IllegalArgumentException("List is immutable");
            } else {
               iterator.remove();
            }
         }

         public void set(T e) {
            if (!PotentiallyUnmodifiableList.this.modifiable) {
               throw new IllegalArgumentException("List is immutable");
            } else {
               iterator.set(e);
            }
         }

         public void add(T e) {
            if (!PotentiallyUnmodifiableList.this.modifiable) {
               throw new IllegalArgumentException("List is immutable");
            } else {
               iterator.add(e);
            }
         }
      };
   }
}
