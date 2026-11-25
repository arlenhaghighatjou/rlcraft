package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTByte extends ITag {
   public final byte data;

   NBTByte(String label, String name, String data) {
      super(label, name);
      this.data = Byte.parseByte(data);
   }

   NBTByte(String label, String name, byte data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setByte(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      cmp.appendTag(new NBTTagByte(this.data));
   }

   public NBTType getType() {
      return NBTType.BYTE;
   }
}
