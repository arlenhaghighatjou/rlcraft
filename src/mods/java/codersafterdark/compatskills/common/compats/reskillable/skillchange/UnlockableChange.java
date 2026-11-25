package codersafterdark.compatskills.common.compats.reskillable.skillchange;

import codersafterdark.reskillable.api.unlockable.Unlockable;
import java.util.Objects;

public abstract class UnlockableChange implements SkillChange {
   protected final Unlockable unlockable;
   private final boolean unlock;

   protected UnlockableChange(Unlockable unlockable, boolean unlock) {
      this.unlockable = unlockable;
      this.unlock = unlock;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.unlockable, this.unlock});
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof UnlockableChange)) {
         return false;
      } else {
         UnlockableChange change = (UnlockableChange)o;
         return this.unlock == change.unlock && this.unlockable.equals(change.unlockable);
      }
   }
}
