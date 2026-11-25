package ivorius.reccomplex.nbt;

import net.minecraft.nbt.NBTBase;

public class NBTDirect implements NBTStorable {
   public NBTBase nbt;

   public NBTDirect(NBTBase nbt) {
      this.nbt = nbt;
   }

   public NBTBase writeToNBT() {
      return this.nbt;
   }
}
