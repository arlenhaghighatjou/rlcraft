package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;

public class NBTShort extends ITag {
   public final short data;

   NBTShort(String label, String name, String data) {
      super(label, name);
      this.data = Short.parseShort(data);
   }

   NBTShort(String label, String name, short data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setShort(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      cmp.appendTag(new NBTTagShort(this.data));
   }

   public NBTType getType() {
      return NBTType.SHORT;
   }
}
