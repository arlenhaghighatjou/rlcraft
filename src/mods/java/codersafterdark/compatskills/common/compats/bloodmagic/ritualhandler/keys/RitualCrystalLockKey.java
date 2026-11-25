package codersafterdark.compatskills.common.compats.bloodmagic.ritualhandler.keys;

import WayofTime.bloodmagic.ritual.Ritual;
import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.LockKey;
import java.util.Objects;

public class RitualCrystalLockKey implements FuzzyLockKey {
   private final int crystalLevel;

   public RitualCrystalLockKey(int crystalLevel) {
      this.crystalLevel = crystalLevel;
   }

   public RitualCrystalLockKey(Ritual ritual) {
      this(ritual == null ? -1 : ritual.getCrystalLevel());
   }

   public boolean equals(Object o) {
      return o == this || o instanceof RitualCrystalLockKey && this.crystalLevel == ((RitualCrystalLockKey)o).crystalLevel;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.crystalLevel});
   }

   public boolean fuzzyEquals(FuzzyLockKey o) {
      return o == this || o instanceof RitualCrystalLockKey && this.crystalLevel >= ((RitualCrystalLockKey)o).crystalLevel;
   }

   public boolean isNotFuzzy() {
      return false;
   }

   public LockKey getNotFuzzy() {
      return null;
   }
}
