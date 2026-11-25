package shadows.placebo.util.collections;

import java.util.ArrayDeque;

public abstract class BlockedDeque<T> extends ArrayDeque<T> {
   private static final long serialVersionUID = -8194197029368437188L;

   public BlockedDeque() {
   }

   public abstract boolean isBlocked(T var1);

   public void addFirst(T t) {
      if (!this.isBlocked(t)) {
         super.addFirst(t);
      }
   }

   public void addLast(T t) {
      if (!this.isBlocked(t)) {
         super.addLast(t);
      }
   }

   public boolean add(T t) {
      this.addLast(t);
      return !this.isBlocked(t);
   }

   public boolean offerFirst(T t) {
      this.addFirst(t);
      return !this.isBlocked(t);
   }

   public boolean offerLast(T t) {
      this.addLast(t);
      return !this.isBlocked(t);
   }
}
