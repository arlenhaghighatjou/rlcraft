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

public class CPathLinear extends CPath {
   public VectorN motionPerSecond;

   public CPathLinear() {
   }

   public CPathLinear(VectorN motionPerSecond) {
      this.motionPerSecond = motionPerSecond;
   }

   public VectorN getRelativePositionInternal(long time) {
      return this.motionPerSecond.copy().scale((double)time / 1000.0D);
   }

   public CPathLinear write(ByteBuf buf) {
      super.write(buf);
      (new CVectorN()).set(this.motionPerSecond).write(buf);
      return this;
   }

   public CPathLinear read(ByteBuf buf) {
      super.read(buf);
      this.motionPerSecond = (new CVectorN()).read(buf).value;
      return this;
   }

   public CPathLinear save(OutputStream stream) {
      super.save(stream);
      (new CVectorN()).set(this.motionPerSecond).save(stream);
      return this;
   }

   public CPathLinear load(InputStream stream) {
      super.load(stream);
      this.motionPerSecond = (new CVectorN()).load(stream).value;
      return this;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = super.serializeNBT();
      if (this.motionPerSecond.values.length > 0) {
         NBTTagList list = new NBTTagList();

         for(double d : this.motionPerSecond.values) {
            list.appendTag(new NBTTagDouble(d));
         }

         compound.setTag("motionPerSecond", list);
      }

      return compound;
   }

   public void deserializeNBT(NBTBase nbt) {
      super.deserializeNBT(nbt);
      NBTTagCompound compound = (NBTTagCompound)nbt;
      this.motionPerSecond = new VectorN(new double[0]);
      if (compound.hasKey("motionPerSecond")) {
         NBTTagList list = (NBTTagList)compound.getTag("motionPerSecond");
         this.motionPerSecond.values = new double[list.tagCount()];

         for(int i = 0; i < this.motionPerSecond.values.length; ++i) {
            this.motionPerSecond.values[i] = list.getDoubleAt(i);
         }
      }

   }
}
