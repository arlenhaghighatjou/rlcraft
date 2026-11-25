package codersafterdark.reskillable.api.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GenericNBTLockKey extends NBTLockKey {
   public GenericNBTLockKey(NBTTagCompound tag) {
      super(tag);
   }

   public GenericNBTLockKey(ItemStack stack) {
      this(stack.getTagCompound());
   }

   public LockKey getNotFuzzy() {
      return null;
   }

   public boolean equals(Object o) {
      boolean var10000;
      if (o != this) {
         label31: {
            if (o instanceof GenericNBTLockKey) {
               if (this.getTag() == null) {
                  if (((GenericNBTLockKey)o).getTag() == null) {
                     break label31;
                  }
               } else if (this.getTag().equals(((GenericNBTLockKey)o).getTag())) {
                  break label31;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public int hashCode() {
      return this.tag == null ? super.hashCode() : this.tag.hashCode();
   }
}
