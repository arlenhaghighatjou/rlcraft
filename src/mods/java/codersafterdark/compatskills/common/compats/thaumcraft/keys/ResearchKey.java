package codersafterdark.compatskills.common.compats.thaumcraft.keys;

import codersafterdark.reskillable.api.data.LockKey;

public class ResearchKey implements LockKey {
   private final String researchKey;

   public ResearchKey(String researchKey) {
      this.researchKey = researchKey == null ? "" : researchKey;
   }

   public boolean equals(Object obj) {
      return obj == this || obj instanceof ResearchKey && this.researchKey.equals(((ResearchKey)obj).researchKey);
   }

   public int hashCode() {
      return this.researchKey.hashCode();
   }
}
