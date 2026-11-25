package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTFromJsonWrapper extends ITag {
   private NBTTagCompound wrapped;

   NBTFromJsonWrapper(String label, String data) {
      super(label, label);

      try {
         this.wrapped = JsonToNBT.getTagFromJson(data);
      } catch (NBTException var4) {
         System.err.println("Exception reading json-nbt string: " + var4.getMessage());
         this.wrapped = new NBTTagCompound();
      }

   }

   public void write(NBTTagCompound cmp) {
      cmp.merge(this.wrapped);
   }

   public void write(NBTTagList in) {
      in.appendTag(this.wrapped.copy());
   }

   public NBTType getType() {
      return NBTType.COMPOUND;
   }
}
