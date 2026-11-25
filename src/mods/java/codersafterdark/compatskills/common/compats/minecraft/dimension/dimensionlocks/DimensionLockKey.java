package codersafterdark.compatskills.common.compats.minecraft.dimension.dimensionlocks;

import codersafterdark.reskillable.api.data.LockKey;
import java.util.Objects;

public class DimensionLockKey implements LockKey {
   private final int dimension;

   public DimensionLockKey(int dimension) {
      this.dimension = dimension;
   }

   public boolean equals(Object obj) {
      return obj == this || obj instanceof DimensionLockKey && this.dimension == ((DimensionLockKey)obj).dimension;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.dimension});
   }
}
