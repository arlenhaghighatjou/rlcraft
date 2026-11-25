package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

public class CPathConstant extends CPath {
   public VectorN position;

   public CPathConstant() {
   }

   public CPathConstant(VectorN position) {
      this.position = position;
   }

   public VectorN getRelativePositionInternal(long time) {
      return this.position.copy();
   }

   public CPathConstant write(ByteBuf buf) {
      super.write(buf);
      (new CVectorN()).set(this.position).write(buf);
      return this;
   }

   public CPathConstant read(ByteBuf buf) {
      super.read(buf);
      this.position = (new CVectorN()).read(buf).value;
      return this;
   }

   public CPathConstant save(OutputStream stream) {
      super.save(stream);
      (new CVectorN()).set(this.position).save(stream);
      return this;
   }

   public CPathConstant load(InputStream stream) {
      super.load(stream);
      this.position = (new CVectorN()).load(stream).value;
      return this;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = super.serializeNBT();
      if (this.position.values.length > 0) {
         NBTTagList list = new NBTTagList();

         for(double d : this.position.values) {
            list.appendTag(new NBTTagDouble(d));
         }

         compound.setTag("position", list);
      }

      return compound;
   }

   public void deserializeNBT(NBTBase nbt) {
      super.deserializeNBT(nbt);
      NBTTagCompound compound = (NBTTagCompound)nbt;
      this.position = new VectorN(new double[0]);
      if (compound.hasKey("position")) {
         NBTTagList list = (NBTTagList)compound.getTag("position");
         this.position.values = new double[list.tagCount()];

         for(int i = 0; i < this.position.values.length; ++i) {
            this.position.values[i] = list.getDoubleAt(i);
         }
      }

   }
}
