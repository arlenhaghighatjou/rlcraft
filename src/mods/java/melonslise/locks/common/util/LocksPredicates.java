package melonslise.locks.common.util;

import java.util.function.Predicate;

public final class LocksPredicates {
   public static final Predicate<Lockable> LOCKED = (lockable) -> {
      return lockable.lock.isLocked();
   };
   public static final Predicate<Lockable> NOT_LOCKED = LOCKED.negate();

   private LocksPredicates() {
   }
}
