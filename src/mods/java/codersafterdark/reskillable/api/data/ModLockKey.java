package codersafterdark.reskillable.api.data;

import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ModLockKey extends NBTLockKey {
   private final String modName;

   public ModLockKey(String modName) {
      this(modName, (NBTTagCompound)null);
   }

   public ModLockKey(String modName, NBTTagCompound tag) {
      super(tag);
      this.modName = modName == null ? "" : modName.toLowerCase();
   }

   public ModLockKey(ItemStack stack) {
      super(stack.getTagCompound());
      ResourceLocation registryName = stack.getItem().getRegistryName();
      this.modName = registryName == null ? "" : registryName.getResourceDomain();
   }

   public LockKey getNotFuzzy() {
      return this.isNotFuzzy() ? this : new ModLockKey(this.modName);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o instanceof ModLockKey && this.getModName().equals(((ModLockKey)o).getModName())) {
         return this.getTag() == null ? ((ModLockKey)o).getTag() == null : this.getTag().equals(((ModLockKey)o).getTag());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.modName, this.tag});
   }

   public String getModName() {
      return this.modName;
   }
}
