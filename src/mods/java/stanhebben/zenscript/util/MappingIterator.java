package stanhebben.zenscript.util;

import java.util.Iterator;

public abstract class MappingIterator<T, U> implements Iterator<U> {
   private final Iterator<T> iterator;

   public MappingIterator(Iterator<T> iterator) {
      this.iterator = iterator;
   }

   public abstract U map(T var1);

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public U next() {
      return (U)this.map(this.iterator.next());
   }

   public void remove() {
      this.iterator.remove();
   }
}
