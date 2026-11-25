package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

public class NBTDouble extends ITag {
   public final double data;

   public NBTDouble(String label, String name, String data) {
      super(label, name);
      this.data = Double.parseDouble(data);
   }

   NBTDouble(String label, String name, double data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setDouble(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      cmp.appendTag(new NBTTagDouble(this.data));
   }

   public NBTType getType() {
      return NBTType.DOUBLE;
   }
}
