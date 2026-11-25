package codersafterdark.compatskills.common.compats.bloodmagic.ritualhandler.keys;

import WayofTime.bloodmagic.ritual.Ritual;
import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.LockKey;
import java.util.Objects;

public class RitualCostLockKey implements FuzzyLockKey {
   private final int activationCost;

   public RitualCostLockKey(int activationCost) {
      this.activationCost = activationCost;
   }

   public RitualCostLockKey(Ritual ritual) {
      this(ritual == null ? -1 : ritual.getActivationCost());
   }

   public boolean equals(Object o) {
      return o == this || o instanceof RitualCostLockKey && this.activationCost == ((RitualCostLockKey)o).activationCost;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.activationCost});
   }

   public boolean fuzzyEquals(FuzzyLockKey o) {
      return o == this || o instanceof RitualCostLockKey && this.activationCost >= ((RitualCostLockKey)o).activationCost;
   }

   public boolean isNotFuzzy() {
      return false;
   }

   public LockKey getNotFuzzy() {
      return null;
   }
}
