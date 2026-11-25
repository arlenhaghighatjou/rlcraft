package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class ITag {
   protected final String label;
   protected final String name;

   protected ITag(String label, String name) {
      this.label = label;
      this.name = name;
   }

   public abstract void write(NBTTagCompound var1);

   public abstract void write(NBTTagList var1);

   public abstract NBTType getType();

   public int hashCode() {
      return this.label.hashCode();
   }

   public boolean equals(Object other) {
      if (!(other instanceof ITag)) {
         return false;
      } else {
         ITag o = (ITag)other;
         return o.label.equals(this.label);
      }
   }
}
