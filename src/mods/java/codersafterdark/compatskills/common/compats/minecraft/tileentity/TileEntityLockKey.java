package codersafterdark.compatskills.common.compats.minecraft.tileentity;

import codersafterdark.reskillable.api.data.LockKey;

public class TileEntityLockKey implements LockKey {
   private final String tileName;

   public TileEntityLockKey(String tileName) {
      this.tileName = tileName == null ? "" : tileName.toLowerCase();
   }

   public boolean equals(Object o) {
      return o == this || o instanceof TileEntityLockKey && this.tileName.equals(((TileEntityLockKey)o).tileName);
   }

   public int hashCode() {
      return this.tileName.hashCode();
   }
}
