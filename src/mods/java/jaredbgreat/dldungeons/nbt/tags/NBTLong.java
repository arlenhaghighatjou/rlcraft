package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;

public class NBTLong extends ITag {
   public final long data;

   NBTLong(String label, String name, String data) {
      super(label, name);
      this.data = Long.parseLong(data);
   }

   NBTLong(String label, String name, long data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setLong(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      cmp.appendTag(new NBTTagLong(this.data));
   }

   public NBTType getType() {
      return NBTType.LONG;
   }
}
