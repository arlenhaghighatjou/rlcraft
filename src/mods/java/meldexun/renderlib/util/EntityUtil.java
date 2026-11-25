package meldexun.renderlib.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;

public class EntityUtil {
   public EntityUtil() {
   }

   public static Iterable<Entity> entityIterable(Iterable<Entity> iterable) {
      return () -> {
         return new Iterator<Entity>() {
            Iterator iterator = iterable.iterator();
            Iterator partIterator;

            public boolean hasNext() {
               return this.partIterator != null && this.partIterator.hasNext() || this.iterator.hasNext();
            }

            public Entity next() {
               if (this.partIterator != null) {
                  if (this.partIterator.hasNext()) {
                     return (Entity)this.partIterator.next();
                  }

                  this.partIterator = null;
               }

               if (!this.iterator.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  Entity entity = (Entity)this.iterator.next();
                  Entity[] parts = entity.getParts();
                  if (parts != null && parts.length > 0) {
                     Iterator partIterator1 = Arrays.stream(parts).filter((e) -> {
                        return e.addedToChunk;
                     }).iterator();
                     if (partIterator1.hasNext()) {
                        this.partIterator = partIterator1;
                     }
                  }

                  return entity;
               }
            }
         };
      };
   }
}
