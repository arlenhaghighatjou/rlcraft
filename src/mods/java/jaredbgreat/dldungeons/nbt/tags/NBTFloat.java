package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

public class NBTFloat extends ITag {
   public final float data;

   NBTFloat(String label, String name, String data) {
      super(label, name);
      this.data = Float.parseFloat(data);
   }

   NBTFloat(String label, String name, float data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setFloat(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      cmp.appendTag(new NBTTagFloat(this.data));
   }

   public NBTType getType() {
      return NBTType.FLOAT;
   }
}
