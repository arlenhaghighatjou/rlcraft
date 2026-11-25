package pl.asie.foamfix.util;

import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.strategy.HashingStrategy;
import pl.asie.foamfix.client.IDeduplicatingStorage;

public class DeduplicatingStorageTrove<T> extends TCustomHashSet<T> implements IDeduplicatingStorage<T> {
   public DeduplicatingStorageTrove(HashingStrategy<T> strategy) {
      super(strategy);
   }

   public T deduplicate(T o) {
      int i = this.index(o);
      if (i >= 0) {
         return (T)this._set[i];
      } else {
         this.add(o);
         return o;
      }
   }
}
