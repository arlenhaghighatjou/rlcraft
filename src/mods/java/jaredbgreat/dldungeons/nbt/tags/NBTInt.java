package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;

public class NBTInt extends ITag {
   public final int data;

   NBTInt(String label, String name, String data) {
      super(label, name);
      this.data = Integer.parseInt(data);
   }

   NBTInt(String label, String name, int data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setInteger(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      cmp.appendTag(new NBTTagInt(this.data));
   }

   public NBTType getType() {
      return NBTType.INT;
   }
}
