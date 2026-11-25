package codersafterdark.compatskills.common.compats.minecraft.item;

import codersafterdark.reskillable.api.data.LockKey;
import codersafterdark.reskillable.api.data.NBTLockKey;
import java.util.Objects;
import net.minecraft.nbt.NBTTagCompound;

public class OreDictLock extends NBTLockKey {
   private final String oreDict;

   public OreDictLock(String oreDict) {
      this(oreDict, (NBTTagCompound)null);
   }

   public OreDictLock(String oreDict, NBTTagCompound tag) {
      super(tag);
      this.oreDict = oreDict == null ? "" : oreDict;
   }

   public LockKey getNotFuzzy() {
      return this.isNotFuzzy() ? this : new OreDictLock(this.oreDict);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o instanceof OreDictLock && this.oreDict.equals(((OreDictLock)o).oreDict)) {
         return this.getTag() == null ? ((OreDictLock)o).getTag() == null : this.getTag().equals(((OreDictLock)o).getTag());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.oreDict, this.tag});
   }
}
