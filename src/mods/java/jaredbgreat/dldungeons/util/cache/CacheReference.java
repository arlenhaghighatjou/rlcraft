package jaredbgreat.dldungeons.util.cache;

import com.arlenh7.eaglercraft.ref.WeakReference;

public class CacheReference<T> extends WeakReference<T> {
   private final WeakCache owner;

   public CacheReference(T referent, WeakCache owner) {
      super(referent);
      this.owner = owner;
   }

   protected void finalize() throws Throwable {
      this.owner.reduce();
      super.finalize();
   }
}
