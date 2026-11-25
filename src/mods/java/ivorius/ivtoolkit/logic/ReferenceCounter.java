package ivorius.ivtoolkit.logic;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReferenceCounter<V> {
   private TObjectIntMap<V> map = new TObjectIntHashMap(10, 0.5F, 0);
   private Set<V> freeObjects = new HashSet();

   public ReferenceCounter() {
   }

   public int getRetainCount(V object) {
      return this.map.get(object);
   }

   public boolean hasObject(V object) {
      return this.map.containsKey(object);
   }

   public void retain(V object, int retainCount) {
      if (retainCount < 1) {
         throw new RuntimeException("Attempting to retain an object by a negative amount - use release instead.");
      } else {
         this.map.adjustOrPutValue(object, retainCount, retainCount);
      }
   }

   public boolean release(V object, int releaseCount) {
      if (releaseCount < 1) {
         throw new RuntimeException("Attempting to release an object by a negative amount - use retain instead.");
      } else {
         int newCount = this.map.adjustOrPutValue(object, -releaseCount, -releaseCount);
         if (newCount < 0) {
            throw new RuntimeException("Attempting to release a freed object!");
         } else {
            if (newCount == 0) {
               this.freeObjects.add(object);
               this.map.remove(object);
            } else {
               this.map.put(object, newCount);
            }

            return newCount > 0;
         }
      }
   }

   public Set<V> deallocateAllFreeObjects() {
      if (this.freeObjects.size() == 0) {
         return Collections.emptySet();
      } else {
         Set<V> freeObjects = this.freeObjects;
         this.freeObjects = new HashSet();
         return freeObjects;
      }
   }

   public int numberOfRetainedObjects() {
      return this.map.size();
   }

   public int numberOfFreeObjects() {
      return this.freeObjects.size();
   }

   public Set<V> getFreeObjects() {
      return Collections.unmodifiableSet(this.freeObjects);
   }

   public Set<V> getAllRetainedObjects() {
      return this.map.keySet();
   }
}
