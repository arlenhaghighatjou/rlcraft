package crafttweaker.util;

import java.io.Closeable;
import java.util.Iterator;

public class CloseablePriorityList<T> implements Iterable<T> {
   private CloseablePriorityList<T>.CloseableEntry first;

   public CloseablePriorityList() {
   }

   public Closeable add(T value) {
      if (this.first == null) {
         this.first = new CloseablePriorityList.CloseableEntry(value);
      } else {
         CloseablePriorityList<T>.CloseableEntry entry = new CloseablePriorityList.CloseableEntry(value);
         entry.next = this.first;
         this.first.prev = entry;
         this.first = entry;
      }

      return this.first;
   }

   public Iterator<T> iterator() {
      return new CloseablePriorityList.MyIterator();
   }

   private class CloseableEntry implements Closeable {
      private final T value;
      private CloseablePriorityList<T>.CloseableEntry next;
      private CloseablePriorityList<T>.CloseableEntry prev;

      public CloseableEntry(T value) {
         this.value = value;
      }

      public void close() {
         if (this.next != null) {
            this.next.prev = this.prev;
         }

         if (this.prev == null) {
            CloseablePriorityList.this.first = this.next;
         } else {
            this.prev.next = this.next;
         }

      }
   }

   private class MyIterator implements Iterator<T> {
      private CloseablePriorityList<T>.CloseableEntry current;

      private MyIterator() {
         this.current = null;
      }

      public boolean hasNext() {
         return this.current == null || this.current.next != null;
      }

      public T next() {
         this.current = this.current == null ? CloseablePriorityList.this.first : this.current.next;
         return this.current.value;
      }

      public void remove() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
