package codersafterdark.reskillable.api.data;

public final class GenericLockKey implements LockKey {
   private Class<? extends LockKey> type;

   public GenericLockKey(Class<? extends LockKey> type) {
      this.type = type;
   }

   public boolean equals(Object o) {
      boolean var10000;
      if (o != this) {
         label31: {
            if (o instanceof GenericLockKey) {
               if (this.type == null) {
                  if (((GenericLockKey)o).type == null) {
                     break label31;
                  }
               } else if (this.type.equals(((GenericLockKey)o).type)) {
                  break label31;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public int hashCode() {
      return this.type == null ? super.hashCode() : this.type.hashCode();
   }
}
