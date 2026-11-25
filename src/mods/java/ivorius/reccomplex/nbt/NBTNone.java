package ivorius.reccomplex.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTNone implements NBTStorable {
   public NBTNone() {
   }

   public NBTBase writeToNBT() {
      return new NBTTagCompound();
   }
}
