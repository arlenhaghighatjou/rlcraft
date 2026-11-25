package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTBoolean extends ITag {
   public final boolean data;

   NBTBoolean(String label, String name, String data) {
      super(label, name);
      this.data = Boolean.parseBoolean(data);
   }

   NBTBoolean(String label, String name, boolean data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setBoolean(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      byte out = 0;
      if (this.data) {
         out = 1;
      }

      NBTTagByte sub = new NBTTagByte(out);
      cmp.appendTag(sub);
   }

   public NBTType getType() {
      return NBTType.BOOLEAN;
   }
}
