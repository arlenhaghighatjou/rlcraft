package codersafterdark.compatskills.common.compats.projecte;

import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.LockKey;
import java.util.Objects;

public class EMCLockKey implements FuzzyLockKey {
   private final long emc;

   public EMCLockKey(long emc) {
      this.emc = emc;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof EMCLockKey && this.emc == ((EMCLockKey)o).emc;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.emc});
   }

   public boolean fuzzyEquals(FuzzyLockKey o) {
      return o == this || o instanceof EMCLockKey && this.emc >= ((EMCLockKey)o).emc;
   }

   public boolean isNotFuzzy() {
      return false;
   }

   public LockKey getNotFuzzy() {
      return null;
   }
}
